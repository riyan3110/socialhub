package com.example.service

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class RouterGatewayService {

  private val TAG = "HermesAI"

  private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

  /**
   * Build endpoint chat dari gateway URL yang dimasukkan user.
   * Target: http://43.159.50.231:20128/v1  →  http://43.159.50.231:20128/v1/chat/completions
   *
   * Smart handling:
   * - Trim whitespace
   * - Remove trailing slash
   * - If URL ends with /v1 or /v1/, strip it first to avoid /v1/v1
   * - If URL ends with /chat/completions, strip it to avoid double append
   * - Always append /v1/chat/completions at the end
   */
  private fun buildChatEndpoint(gatewayUrl: String): String {
    var base = gatewayUrl.trim()

    // Remove trailing slash
    if (base.endsWith("/")) {
      base = base.dropLast(1)
    }

    // Remove /chat/completions if already present (avoid double append)
    if (base.endsWith("/chat/completions")) {
      base = base.removeSuffix("/chat/completions")
    }

    // Remove /v1 if already present (avoid /v1/v1)
    if (base.endsWith("/v1")) {
      base = base.removeSuffix("/v1")
    }

    // Always append exactly /v1/chat/completions
    return "$base/v1/chat/completions"
  }

  private fun friendlyHttpError(code: Int): String {
    return when (code) {
      400 -> "400 Bad Request — Format permintaan salah. Cek URL, API Key, dan Body request."
      401 -> "401 Unauthorized — API Key salah atau tidak valid."
      403 -> "403 Forbidden — Tidak punya akses ke gateway ini."
      404 -> "404 Not Found — Endpoint tidak ditemukan. Pastikan Base URL sudah benar."
      408 -> "408 Request Timeout — Gateway tidak merespon tepat waktu."
      429 -> "429 Too Many Requests — Terlalu banyak permintaan. Tunggu sebentar lalu coba lagi."
      500 -> "500 Internal Server Error — Ada masalah di server gateway."
      502 -> "502 Bad Gateway — Gateway menerima respons tidak valid dari server."
      503 -> "503 Service Unavailable — Gateway sedang maintenance atau sibuk."
      504 -> "504 Gateway Timeout — Gateway timeout saat menghubungi server."
      else -> "HTTP Error $code — Respons tidak dikenali dari server."
    }
  }

  suspend fun callGateway(
    gatewayUrl: String,
    authToken: String,
    model: String,
    messages: List<Map<String, String>>,
    systemPrompt: String? = null
  ): String = withContext(Dispatchers.IO) {

    // === LOGGING INPUT ===
    Log.i(TAG, "===== HermesAI Request =====")
    Log.i(TAG, "Gateway URL input : '$gatewayUrl'")
    Log.i(TAG, "Model            : '$model'")
    Log.i(TAG, "Auth Token       : '${authToken.take(8)}...' (length=${authToken.length})")
    Log.i(TAG, "Messages count   : ${messages.size}")
    Log.i(TAG, "System Prompt    : ${systemPrompt ?: "(none)"}")

    // === VALIDASI ===
    if (gatewayUrl.isBlank()) {
      Log.e(TAG, "VALIDATION FAILED: Gateway URL kosong")
      throw Exception("Masukkan Gateway URL")
    }
    if (authToken.isBlank()) {
      Log.e(TAG, "VALIDATION FAILED: API Key kosong")
      throw Exception("Masukkan API Key")
    }

    // Build endpoint
    val endpoint = buildChatEndpoint(gatewayUrl)
    Log.i(TAG, "Computed endpoint: '$endpoint'")

    // Build messages JSON
    val jsonMessages = JSONArray()
    if (!systemPrompt.isNullOrBlank()) {
      jsonMessages.put(JSONObject().put("role", "system").put("content", systemPrompt))
      Log.i(TAG, "Added system prompt (${systemPrompt.length} chars)")
    }
    for (msg in messages) {
      val role = msg["role"] ?: "user"
      val content = msg["content"] ?: ""
      jsonMessages.put(JSONObject().put("role", role).put("content", content))
      Log.i(TAG, "Added message role='$role' (${content.length} chars)")
    }

    // Build body
    val finalModel = if (model.isNotBlank()) model else "My2"
    val jsonBody = JSONObject().apply {
      put("model", finalModel)
      put("messages", jsonMessages)
    }
    val requestBodyString = jsonBody.toString(2)
    val requestBody = requestBodyString.toRequestBody("application/json; charset=utf-8".toMediaType())

    Log.i(TAG, "Request body (pretty):\n$requestBodyString")

    // Build OkHttp request
    val request = Request.Builder()
      .url(endpoint)
      .addHeader("Authorization", "Bearer $authToken")
      .addHeader("Content-Type", "application/json")
      .post(requestBody)
      .build()

    Log.i(TAG, "HTTP Method: POST")
    Log.i(TAG, "URL: ${request.url}")
    Log.i(TAG, "Headers: Authorization=Bearer ${authToken.take(12)}..., Content-Type=application/json")
    Log.i(TAG, "Sending request...")

    try {
      client.newCall(request).execute().use { response ->
        val statusCode = response.code
        val responseBodyString = response.body?.string() ?: "(empty body)"
        val errorBodyString = if (statusCode !in 200..299) responseBodyString else "(no error body)"

        Log.i(TAG, "HTTP Status Code: $statusCode")
        Log.i(TAG, "Response body ($statusCode):\n$responseBodyString")

        if (!response.isSuccessful) {
          Log.e(TAG, "Request FAILED - HTTP $statusCode")
          Log.e(TAG, "Error body: $errorBodyString")
          val friendlyMsg = friendlyHttpError(statusCode)
          throw Exception("$friendlyMsg\n\nDetail dari server:\n$errorBodyString")
        }

        // === Parsing Response ===
        Log.i(TAG, "Request SUCCESS - parsing response...")
        val responseJson = JSONObject(responseBodyString)
        val choices = responseJson.optJSONArray("choices")
          ?: throw Exception("Invalid gateway response format: missing 'choices' array.\nFull response: $responseBodyString")

        if (choices.length() == 0) throw Exception("Gateway returned empty choices array.\nFull response: $responseBodyString")

        val firstChoice = choices.getJSONObject(0)
        val messageObj = firstChoice.optJSONObject("message")
          ?: throw Exception("Invalid choice format: missing 'message' object.\nFull response: $responseBodyString")

        val content = messageObj.optString("content", "").trim()
        if (content.isBlank()) {
          Log.w(TAG, "Response content is blank!")
          throw Exception("AI mengembalikan respons kosong. Coba prompt yang berbeda.")
        }

        Log.i(TAG, "Response content (${content.length} chars): $content")
        Log.i(TAG, "===== HermesAI Request Complete =====")
        return@withContext content
      }
    } catch (e: java.net.SocketTimeoutException) {
      Log.e(TAG, "TIMEOUT: ${e.localizedMessage}", e)
      throw Exception("Koneksi ke gateway timeout.\nCek URL gateway atau jaringan internet Anda.\n\nDetail: ${e.localizedMessage}")
    } catch (e: java.net.UnknownHostException) {
      Log.e(TAG, "UNKNOWN HOST: ${e.localizedMessage}", e)
      throw Exception("Tidak dapat menemukan host.\nPeriksa URL gateway: '$endpoint'\n\nPastikan tidak ada typo dan perangkat terhubung ke internet.")
    } catch (e: javax.net.ssl.SSLException) {
      Log.e(TAG, "SSL ERROR: ${e.localizedMessage}", e)
      throw Exception("Kesalahan koneksi aman (SSL).\nCoba ganti https:// menjadi http:// jika gateway tidak pakai SSL.\n\nDetail: ${e.localizedMessage}")
    } catch (e: java.io.IOException) {
      Log.e(TAG, "NETWORK IO ERROR: ${e.localizedMessage}", e)
      throw Exception("Kesalahan jaringan.\nPeriksa koneksi internet Anda.\n\nDetail: ${e.localizedMessage}")
    } catch (e: Exception) {
      Log.e(TAG, "UNEXPECTED ERROR: ${e.localizedMessage}", e)
      throw Exception("Error: ${e.localizedMessage}")
    }
  }

  /**
   * Test koneksi ke gateway.
   * Return Pair<Boolean, String> — pertama = sukses/gagal, kedua = pesan (success atau error detail)
   */
  suspend fun testConnection(
    gatewayUrl: String,
    authToken: String,
    model: String
  ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
    Log.i(TAG, "===== Test Connection =====")
    Log.i(TAG, "Gateway URL : '$gatewayUrl'")
    Log.i(TAG, "Model       : '$model'")

    try {
      val endpoint = buildChatEndpoint(gatewayUrl)
      Log.i(TAG, "Test endpoint: '$endpoint'")

      val response = callGateway(
        gatewayUrl = gatewayUrl,
        authToken = authToken,
        model = model,
        messages = listOf(mapOf("role" to "user", "content" to "hi")),
        systemPrompt = "Respond with exactly: Connection Success"
      )
      Log.i(TAG, "Test connection SUCCESS: $response")
      Log.i(TAG, "===== Test Connection End =====")
      Pair(true, "Connection Success")
    } catch (e: Exception) {
      Log.e(TAG, "Test connection FAILED: ${e.localizedMessage}")
      Log.e(TAG, "===== Test Connection End (FAILED) =====")
      Pair(false, e.localizedMessage ?: "Unknown error occurred")
    }
  }

  suspend fun generateAiResponse(
    toolTitle: String,
    userPrompt: String,
    chatHistory: List<Map<String, String>>?,
    gatewayUrl: String,
    authToken: String,
    model: String
  ): String = withContext(Dispatchers.IO) {
    Log.i(TAG, "===== AI Feature: $toolTitle =====")
    Log.i(TAG, "User prompt: '$userPrompt'")

    val systemPrompt = when (toolTitle) {
      "AI Chat" -> "You are a helpful general AI assistant in Social Hub V2. Provide clear, informative, and engaging responses. Support Markdown formatting."
      "AI Writer" -> "You are an expert AI copywriter. Generate professional captions, posts, articles, and replies based on the user prompt. Support Markdown."
      "AI Translate" -> "You are a professional translator. Translate the given text accurately between languages maintaining a natural, fluent tone. Support Markdown."
      "AI Summarizer" -> "You are an expert summarizer. Summarize the long text, articles, or conversations into clear key points. Support Markdown."
      "AI Grammar" -> "You are an expert copyeditor. Correct grammar, spelling, and phrasing while preserving the original meaning. Support Markdown."
      "AI Rewrite" -> "You are an expert content rewriter. Rewrite the provided text in different professional/creative styles and tones. Support Markdown."
      "AI Image Prompt" -> "You are an expert AI prompt engineer for image generators (Midjourney, DALL-E). Generate rich, detailed, professional visual prompts. Support Markdown."
      "AI Video Prompt" -> "You are an expert AI prompt engineer for video generators (Sora, Runway). Generate cinematic, high-definition video prompts. Support Markdown."
      "AI Hashtag Generator" -> "You are a social media growth expert. Generate optimized, trending hashtags for social media based on the topic. Support Markdown."
      "AI Bio Generator" -> "You are a social media branding expert. Generate engaging, catchy social media bios. Support Markdown."
      "AI Caption Generator" -> "You are an engaging social media creator. Generate catchy, high-engagement captions with emojis for posts. Support Markdown."
      "AI Content Ideas" -> "You are a social media strategist. Generate creative, actionable content ideas for TikTok, Instagram, Facebook, and X. Support Markdown."
      else -> "You are a helpful AI assistant. Support Markdown."
    }

    val messages = mutableListOf<Map<String, String>>()
    if (chatHistory != null && chatHistory.isNotEmpty()) {
      messages.addAll(chatHistory)
    }
    messages.add(mapOf("role" to "user", "content" to userPrompt))

    callGateway(
      gatewayUrl = gatewayUrl,
      authToken = authToken,
      model = model,
      messages = messages,
      systemPrompt = systemPrompt
    )
  }
}
