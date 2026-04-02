package com.example.cattlemanagement

import com.google.ai.client.generativeai.GenerativeModel

class GeminiRepository {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun sendMessage(userMessage: String): String {
        return try {
            val prompt = """
                You are an AI assistant inside a Cattle Management System Android app.
                Help with cattle health, feed, breeding, milk production, vaccination, and farm management.
                Reply only in simple English.
                If symptoms look serious, advise consulting a veterinarian.

                User: $userMessage
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            response.text ?: "No response generated."
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    suspend fun translateToHindi(englishText: String): String {
        return try {
            val prompt = """
                Translate the following English text into very simple Hindi for Indian farmers.
                Keep the meaning same.
                Do not explain.
                Do not add extra points.
                Only return Hindi translation.

                Text:
                $englishText
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            response.text ?: "हिंदी अनुवाद उपलब्ध नहीं है।"
        } catch (e: Exception) {
            "अनुवाद में त्रुटि: ${e.message}"
        }
    }
}