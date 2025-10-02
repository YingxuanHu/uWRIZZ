package com.example.uwrizz.model

data class Message(
    val id: String = "",
    val receiverId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val chatId: String = ""
)
