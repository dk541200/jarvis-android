package com.jarvis.android

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object OpenRouter {
    suspend fun ask(
        key: String,
        prompt: String,
        recentConversation: List<String> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            connection = (URL("https://openrouter.ai/api/v1/chat/completions")
                .openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15000
                readTimeout = 30000
                setRequestProperty("Authorization", "Bearer $key")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }

            val messages = JSONArray()
            recentConversation.takeLast(12).forEach { entry ->
                val role = when {
                    entry.startsWith("You: ") -> "user"
                    entry.startsWith("JARVIS: ") -> "assistant"
                    else -> null
                }
                if (role != null) {
                    val content = entry.substringAfter(": ").trim()
                    if (content.isNotBlank()) {
                        messages.put(JSONObject().put("role", role).put("content", content))
                    }
                }
            }
            messages.put(JSONObject().put("role", "user").put("content", prompt))

            val body = JSONObject()
                .put("model", "openrouter/auto")
                .put("messages", messages)
                .toString()

            connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val response = stream?.bufferedReader()?.use { it.readText() }.orEmpty()

            if (status !in 200..299) {
                val detail = runCatching {
                    JSONObject(response).optJSONObject("error")?.optString("message")
                        ?: JSONObject(response).optString("error").takeIf { it.isNotBlank() }
                        ?: response
                }.getOrDefault(response)
                "AI provider error $status: $detail"
            } else {
                JSONObject(response).getJSONArray("choices")
                    .getJSONObject(0).getJSONObject("message")
                    .optString("content").ifBlank { "The AI provider returned an empty reply." }
            }
        } catch (e: Exception) {
            "Could not reach OpenRouter: ${e.message ?: "unknown network error"}"
        } finally {
            connection?.disconnect()
        }
    }
}
