package com.example.service

import android.util.Log
import com.example.BuildConfig
import com.example.data.entities.SocialFeedEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAssistantService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    private val apiKey: String = try {
        BuildConfig.GEMINI_API_KEY
    } catch (e: Exception) {
        ""
    }

    private val isApiKeyValid = apiKey.isNotBlank() && !apiKey.contains("MY_GEMINI_API_KEY")

    suspend fun generateFeedSummary(feeds: List<SocialFeedEntity>): String = withContext(Dispatchers.IO) {
        if (feeds.isEmpty()) {
            return@withContext "Belum ada aktivitas sosial media terkini untuk dirangkum."
        }

        if (isApiKeyValid) {
            try {
                val feedTextList = feeds.take(10).joinToString("\n- ") { feed ->
                    "[${feed.platform}] ${feed.authorName}: ${feed.content}"
                }

                val prompt = """
                    Kamu adalah Asisten AI Ringkasan Sosial Media "SocialHub".
                    Berikut adalah kumpulan postingan dan pesan terbaru dari Telegram, WhatsApp, Facebook, Instagram, Twitter/X, dan TikTok:
                    $feedTextList

                    Buatlah ringkasan eksekutif 2-3 kalimat yang singkat, jelas, dan informatif dalam bahasa Indonesia. Highlight poin penting atau pesan mendesak.
                """.trimIndent()

                val apiResult = callGeminiApi(prompt)
                if (!apiResult.isNullOrBlank()) {
                    return@withContext apiResult
                }
            } catch (e: Exception) {
                Log.e("GeminiService", "Error generating summary: ${e.localizedMessage}")
            }
        }

        // Offline Smart Fallback
        val unreadCount = feeds.count { it.isUnread }
        val platformNames = feeds.map { it.platform }.distinct().joinToString(", ")
        val topPost = feeds.maxByOrNull { it.likesCount }

        "✨ **Ringkasan Aktivitas AI**: Anda memiliki $unreadCount aktivitas baru dari platform ($platformNames). Postingan paling populer adalah dari **${topPost?.authorName ?: "Pengguna"}** (${topPost?.platform}) dengan ${topPost?.likesCount ?: 0} suka."
    }

    suspend fun generateSmartReplies(messageContent: String, authorName: String): List<String> = withContext(Dispatchers.IO) {
        if (isApiKeyValid) {
            try {
                val prompt = """
                    Buatkan 3 opsi balasan singkat (maksimal 10 kata per opsi) yang sopan dan ramah dalam bahasa Indonesia untuk pesan berikut dari $authorName:
                    "$messageContent"
                    Format output: hanya 3 baris terpisah tanpa nomor.
                """.trimIndent()

                val apiResult = callGeminiApi(prompt)
                if (!apiResult.isNullOrBlank()) {
                    val lines = apiResult.lines().map { it.trim().removePrefix("-").removePrefix("1.").removePrefix("2.").removePrefix("3.").trim() }.filter { it.isNotBlank() }
                    if (lines.size >= 3) {
                        return@withContext lines.take(3)
                    }
                }
            } catch (e: Exception) {
                Log.e("GeminiService", "Error generating replies: ${e.localizedMessage}")
            }
        }

        // Smart Local Fallback Replies
        listOf(
            "Siap, terima kasih informasinya! 👍",
            "Bolehkah saya minta detail selengkapnya?",
            "Baik, akan segera saya periksa dan kabari lagi."
        )
    }

    suspend fun enhanceCaption(caption: String): String = withContext(Dispatchers.IO) {
        if (caption.isBlank()) return@withContext caption

        if (isApiKeyValid) {
            try {
                val prompt = """
                    Rapikan dan tingkatkan caption sosial media berikut agar lebih menarik, berikan emoji yang sesuai, dan tambahkan 4 hashtag relevan:
                    "$caption"
                    Output hanya berupa caption akhir.
                """.trimIndent()

                val apiResult = callGeminiApi(prompt)
                if (!apiResult.isNullOrBlank()) {
                    return@withContext apiResult.trim()
                }
            } catch (e: Exception) {
                Log.e("GeminiService", "Error enhancing caption: ${e.localizedMessage}")
            }
        }

        // Fallback
        "$caption ✨🚀 #SocialHub #MultiPosting #IndonesiaTech #CreativeContent"
    }

    private fun callGeminiApi(promptText: String): String? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

        val jsonBody = JSONObject().apply {
            val contents = org.json.JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val parts = org.json.JSONArray().apply {
                        put(JSONObject().put("text", promptText))
                    }
                    put("parts", parts)
                }
                put(contentObj)
            }
            put("contents", contents)
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val responseBodyString = response.body?.string() ?: return null
            val responseJson = JSONObject(responseBodyString)
            val candidates = responseJson.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null
            return parts.getJSONObject(0).optString("text")
        }
    }
}
