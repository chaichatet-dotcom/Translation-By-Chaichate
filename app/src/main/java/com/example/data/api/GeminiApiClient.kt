package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com"
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Translates text streaming back chunks.
     * Matches the User's choice of Direct Translation or AI Natural Translation,
     * along with custom tones (Friendly, Professional, Romantic, Funny, Casual, etc.).
     */
    fun translateStream(
        text: String,
        sourceLang: String,
        targetLang: String,
        mode: String, // "Direct" vs "AI Natural"
        tone: String, // Friendly, Professional, Casual, Romantic, Funny
        grammarCorrection: Boolean = false
    ): Flow<String> = flow {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            emit("API Key is missing. Please add GEMINI_API_KEY in your Secrets panel inside AI Studio.")
            return@flow
        }

        // Prompt Engineering for human-like translation
        val systemPrompt = buildSystemPrompt(sourceLang, targetLang, mode, tone, grammarCorrection)
        val url = "$BASE_URL/v1beta/models/gemini-3.5-flash:streamGenerateContent?key=$apiKey"

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().put(
                JSONObject().put("parts", JSONArray().put(
                    JSONObject().put("text", "Text to translate:\n\"\"\"\n$text\n\"\"\"")
                ))
            ))
            put("systemInstruction", JSONObject().put("parts", JSONArray().put(
                JSONObject().put("text", systemPrompt)
            )))
            put("generationConfig", JSONObject().apply {
                put("temperature", if (mode == "Direct") 0.1 else 0.7)
            })
        }

        val body = requestJson.toString().toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    emit("Error: HTTP ${response.code} ${response.message}")
                    return@flow
                }

                val source = response.body?.source() ?: return@flow
                val bufferedReader = BufferedReader(source.inputStream().reader())
                var line: String?

                // Process streaming blocks.
                // The stream content from streamGenerateContent is formatted as an array of JSON objects.
                // Each line is typically a chunk or part of the outer JSON.
                val builder = StringBuilder()
                while (bufferedReader.readLine().also { line = it } != null) {
                    val rawLine = line ?: continue
                    builder.append(rawLine)

                    // Find and extract text candidates from the streaming buffer
                    val textChunks = parseStreamedTextToken(rawLine)
                    if (textChunks.isNotEmpty()) {
                        emit(textChunks)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error performing stream translation", e)
            emit("Failed to connect to translation server: ${e.localizedMessage}")
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Prompt structure matching design objectives:
     * - "Direct Translation" performs clean, precise but contextual output.
     * - "AI Natural Translation" produces extremely colloquial, smooth, authentic translations.
     */
    private fun buildSystemPrompt(
        sourceLang: String,
        targetLang: String,
        mode: String,
        tone: String,
        grammarCorrection: Boolean
    ): String {
        val base = """
            You are "Smart Translate AI", an elite expert human translator. 
            Translate from local "$sourceLang" to native "$targetLang" with maximum cultural accuracy, authentic nuances, and correct emotions.
            ${if (grammarCorrection) "First quietly correct any typing or grammar issues in the source text before translating." else ""}
            
            ${if (mode == "Direct") {
                "Mode: DIRECT. Translate clearly and preserve original sentence structures where grammatically sensible, avoiding unnecessary creative embellishments, but keeping it culturally clear."
            } else {
                "Mode: AI NATURAL. Translate like a loving native speaker in a human conversation. Capture casual speech, slang, idioms, emotional undercurrents, and correct natural phrases. Absolute ban on robotic literal structures."
            }}
            
            Additional instruction for TONE = "$tone":
            - Friendly: Make the wording warm, welcoming, polite, and caring. Add appropriate respectful pronouns.
            - Professional: Match diplomatic business-level formality, high-end vocabulary, and utmost respect.
            - Casual: Translate using everyday relaxed phrasing, appropriate modern slang, and a friendly vibe.
            - Romantic: Exude emotional depth, love, poetic flow, and graceful affection.
            - Funny: Playful, humorous, witty, using funny native idioms where applicable.
            - Natural: Perfect, authentic native speaker tone.
            
            CRITICAL OUTPUT FORMAT RULE:
            - Respond with ONLY the final translation result.
            - Do NOT include 'Here is the translation', 'Translation:', introductory lines, or markdown annotations like triple-backticks.
            - Output ONLY the translated text itself.
        """.trimIndent()
        return base
    }

    /**
     * Utilizes a highly robust regex to extract partial tokens of text from raw streaming json lines.
     * This avoids block-level JSON structural breakage when lines are partial.
     */
    private fun parseStreamedTextToken(jsonLine: String): String {
        try {
            // Find "text": "..." within the string using org.json if it is a complete block,
            // or perform simple substring matching to grab the text fragment.
            if (jsonLine.contains("\"text\"")) {
                // If it looks like a valid line, parse it safely
                val cleaned = jsonLine.trim()
                if (cleaned.startsWith(",") || cleaned.startsWith("[")) {
                    // Try to strip array formatting
                    val sanitized = cleaned.removePrefix("[").removePrefix(",").removePrefix("]").trim()
                    if (sanitized.startsWith("{") && sanitized.endsWith("}")) {
                        val obj = JSONObject(sanitized)
                        val candidates = obj.optJSONArray("candidates")
                        val partText = candidates?.optJSONObject(0)
                            ?.optJSONObject("content")
                            ?.optJSONArray("parts")
                            ?.optJSONObject(0)
                            ?.optString("text")
                        if (!partText.isNullOrEmpty()) {
                            return partText
                        }
                    }
                } else if (cleaned.startsWith("{") && cleaned.endsWith("}")) {
                    val obj = JSONObject(cleaned)
                    val candidates = obj.optJSONArray("candidates")
                    val partText = candidates?.optJSONObject(0)
                        ?.optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text")
                    if (!partText.isNullOrEmpty()) {
                        return partText
                    }
                }

                // Fallback Regex for robust partial matching
                val regex = "\"text\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"".toRegex()
                val match = regex.find(jsonLine)
                if (match != null) {
                    val rawVal = match.groupValues[1]
                    // Unescape typical characters to present clean rendered string
                    return rawVal.replace("\\n", "\n")
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\")
                        .replace("\\t", "\t")
                }
            }
        } catch (e: Exception) {
            // Silence JSON parse failures for streaming fragments
        }
        return ""
    }

    /**
     * Explains the grammar rules, cultural nuances, or interesting vocab of a translated phrase.
     */
    fun explainPhrase(phrase: String, sourceLang: String, targetLang: String): Flow<String> = flow {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            emit("Configure GEMINI_API_KEY to see grammatical breakdowns.")
            return@flow
        }

        val url = "$BASE_URL/v1beta/models/gemini-3.5-flash:streamGenerateContent?key=$apiKey"
        val instruction = """
            You are an expert linguistics coach. Explain the query phrase "$phrase" translated between $sourceLang and $targetLang.
            Provide:
            1. Short linguistic breakdown.
            2. Interesting vocabulary words or cultural idiom notes.
            3. Accent/pronunciation tips.
            Keep it extremely structured, clear, motivating, and beautifully formatted (using bullet points and bolding).
            Make the tone highly engaging and friendly, like Duolingo combined with a professional college tutor.
        """.trimIndent()

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().put(
                JSONObject().put("parts", JSONArray().put(
                    JSONObject().put("text", instruction)
                ))
            ))
        }

        val body = requestJson.toString().toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    emit(" Labeled explanation currently unavailable.")
                    return@flow
                }
                val source = response.body?.source() ?: return@flow
                val bufferedReader = BufferedReader(source.inputStream().reader())
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    val rawLine = line ?: continue
                    val chunk = parseStreamedTextToken(rawLine)
                    if (chunk.isNotEmpty()) {
                        emit(chunk)
                    }
                }
            }
        } catch (e: Exception) {
            emit("Error: ${e.localizedMessage}")
        }
    }.flowOn(Dispatchers.IO)
}
