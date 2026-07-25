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

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun callGateway(
        gatewayUrl: String,
        authToken: String,
        model: String,
        messages: List<Map<String, String>>,
        systemPrompt: String? = null
    ): String = withContext(Dispatchers.IO) {
        if (gatewayUrl.isBlank()) {
            throw Exception("Gateway URL is not configured. Please configure in Hidden Admin Settings.")
        }
        if (authToken.isBlank()) {
            throw Exception("Authentication token is missing. Please configure in Hidden Admin Settings.")
        }

        val formattedUrl = if (gatewayUrl.endsWith("/")) "${gatewayUrl}chat/completions" else "$gatewayUrl/chat/completions"

        val jsonMessages = JSONArray()
        if (!systemPrompt.isNullOrBlank()) {
            jsonMessages.put(JSONObject().put("role", "system").put("content", systemPrompt))
        }
        for (msg in messages) {
            jsonMessages.put(JSONObject().put("role", msg["role"] ?: "user").put("content", msg["content"] ?: ""))
        }

        val jsonBody = JSONObject().apply {
            put("model", if (model.isNotBlank()) model else "gpt-4o")
            put("messages", jsonMessages)
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(formattedUrl)
            .addHeader("Authorization", "Bearer $authToken")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string() ?: "Unknown error"
                    throw Exception("Gateway error (${response.code}): $errorBody")
                }
                val responseBodyString = response.body?.string() ?: throw Exception("Empty response from gateway")
                val responseJson = JSONObject(responseBodyString)
                val choices = responseJson.optJSONArray("choices") ?: throw Exception("Invalid gateway response format: missing choices")
                if (choices.length() == 0) throw Exception("Empty choices from gateway")
                val firstChoice = choices.getJSONObject(0)
                val messageObj = firstChoice.optJSONObject("message") ?: throw Exception("Invalid choice format: missing message")
                return@withContext messageObj.optString("content", "").trim()
            }
        } catch (e: java.net.SocketTimeoutException) {
            throw Exception("Gateway connection timed out. Please check network or gateway status.")
        } catch (e: java.io.IOException) {
            throw Exception("Network error connecting to 9Router Gateway: ${e.localizedMessage}")
        } catch (e: Exception) {
            throw Exception("Error: ${e.localizedMessage}")
        }
    }

    suspend fun testConnection(gatewayUrl: String, authToken: String, model: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = callGateway(
                gatewayUrl = gatewayUrl,
                authToken = authToken,
                model = model,
                messages = listOf(mapOf("role" to "user", "content" to "ping")),
                systemPrompt = "Respond with 'pong'."
            )
            response.isNotBlank()
        } catch (e: Exception) {
            Log.e("RouterGateway", "Test connection failed: ${e.localizedMessage}")
            false
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
