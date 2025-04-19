package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.healthcareproject.data.source.local.Converters
import com.example.healthcareproject.domain.model.MeasurementType
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
    val userId: String,
    val type: MeasurementType,
    val value: Float?,
    val valueList: List<Float>?,
    val timestamp: LocalDateTime
)
