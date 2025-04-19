package com.example.healthcareproject.domain.model

import java.time.LocalDateTime

data class Notification(
    val notificationId: String,
    val userId: String,
    val type: NotificationType,
    val relatedTable: RelatedTable,
    val relatedId: String,
    val message: String,
    val timestamp: LocalDateTime
)