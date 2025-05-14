package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.healthcareproject.data.source.local.Converters
import java.time.LocalDateTime

@Entity(
    tableName = "alert",
    foreignKeys = [
        ForeignKey(
            entity = RoomUser::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoomMeasurement::class,
            parentColumns = ["measurementId"],
            childColumns = ["measurementId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = RoomEmergencyInfo::class,
            parentColumns = ["emergencyId"],
            childColumns = ["emergencyId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("userId"), Index("measurementId"), Index("emergencyId")]
)
@TypeConverters(Converters::class)
data class RoomAlert(
    @PrimaryKey val alertId: String,
    val userId: String,
    val measurementId: String?,
    val emergencyId: String?,
    val triggerReason: String,
    val contacted: Boolean,
    val timestamp: LocalDateTime
)