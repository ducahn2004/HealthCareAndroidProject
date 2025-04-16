package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "notifications",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("relatedTable"), Index("relatedId")]
)
data class Notification(
    @PrimaryKey val notificationId: String,
    val userId: String,
    val type: NotificationType,
    val relatedTable: RelatedTable,
    val relatedId: String,
    val message: String,
    val timestamp: LocalDateTime
)

enum class NotificationType {
    Appointment,
    Alert,
    Sos,
    EmergencyInfo,
    Measurement
}

enum class RelatedTable {
    Appointment,
    Alert,
    Sos,
    EmergencyInfo,
    Measurement
}