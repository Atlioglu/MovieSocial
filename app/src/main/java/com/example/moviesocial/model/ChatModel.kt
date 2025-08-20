package com.example.moviesocial.model


data class ChatModel(
    val message : String,
    val role : String,
)


data class FunctionCall(
    val type: String,
    val label: String,
    val action: String,
    val movieName: String? = null
)