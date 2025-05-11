package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.healthcareproject.data.source.local.Converters
import java.time.LocalDateTime

@Entity(
    tableName = "measurement",
    foreignKeys = [
        ForeignKey(
            entity = RoomUser::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
@TypeConverters(Converters::class)
data class RoomMeasurement(
    @PrimaryKey val measurementId: String,
    val deviceId: String,
    val userId: String,
    val bpm: Float,
    val spO2: Float,
    val dateTime: LocalDateTime
)
