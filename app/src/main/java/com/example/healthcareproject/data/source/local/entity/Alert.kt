package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(
    tableName = "alerts",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class Alert(
    @PrimaryKey val alertId: String,
    val userId: String,
    val title: String,
    val message: String,
    val alertTime: LocalTime,
    val repeatPattern: RepeatPattern,
    val status: Boolean,
    val createdAt: LocalDateTime
)

enum class RepeatPattern {
    Daily,
    Weekly,
    Monthly,
    Yearly
}
