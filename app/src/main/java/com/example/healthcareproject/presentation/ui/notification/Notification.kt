package com.example.healthcareproject.presentation.ui.notification

data class Notification(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String,
    val time: String,
    val iconResId: Int
)