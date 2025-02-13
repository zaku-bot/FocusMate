package com.example.proj5.response

data class ChatRequest(
    val messages: List<Message>,
    val model: String
)