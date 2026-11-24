package com.jarvis.android

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object OpenRouter {
    suspend fun ask(key:String,prompt:String):String=withContext(Dispatchers.IO){
        try{
            val c=URL("https://openrouter.ai/api/v1/chat/completions").openConnection() as HttpURLConnection
            c.requestMethod="POST"; c.connectTimeout=15000; c.readTimeout=30000
            c.setRequestProperty("Authorization","Bearer $key")
            c.setRequestProperty("Content-Type","application/json")
            c.doOutput=true
            val body=JSONObject().put("model","openrouter/auto")
                .put("messages",JSONArray().put(JSONObject().put("role","user").put("content",prompt)))
                .toString()
            c.outputStream.use{it.write(body.toByteArray())}
            val stream=if(c.responseCode in 200..299)c.inputStream else c.errorStream
            val result=stream.bufferedReader().use{it.readText()}
            if(c.responseCode !in 200..299) "AI provider error ${c.responseCode}: ${JSONObject(result).optString("error",result)}"
            else JSONObject(result).getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
        }catch(e:Exception){"Could not reach OpenRouter: ${e.message ?: "unknown network error"}"}
    }
}
