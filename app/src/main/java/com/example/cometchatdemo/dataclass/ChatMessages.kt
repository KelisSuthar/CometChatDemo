package com.example.cometchatdemo.dataclass

data class ChatMessages(
    val msg: String? = "",
    val url: String? = "",
    val sender: String? = "",
    val sender_name: String? = "",
    val is_image: Boolean? = false,
    val message_time: String? = "0",
)
