package com.example.cattlemanagement

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    var hindiText: String? = null
)