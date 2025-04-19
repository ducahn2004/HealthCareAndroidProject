package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity(
    tableName = "sos",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Measurement::class,
            parentColumns = ["measurementId"],
            childColumns = ["measurementId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = EmergencyInfo::class,
            parentColumns = ["emergencyId"],
            childColumns = ["emergencyId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("userId"), Index("measurementId"), Index("emergencyId")]
)
data class Sos(
    @PrimaryKey val sosId: String,
    val userId: String,
    val measurementId: String?,
    val emergencyId: String?,
    val triggerReason: String,
    val contacted: Boolean,
    val timestamp: LocalDateTime
)
