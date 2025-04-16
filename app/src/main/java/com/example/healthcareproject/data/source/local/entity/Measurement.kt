package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "measurement",
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
data class Measurement(
    @PrimaryKey val measurementId: String,
    val userId: String,
    val type: MeasurementType,
    val value: Float,
    val timestamp: LocalDateTime
)

enum class MeasurementType {
    SpO2,
    HR,
    ECG
}