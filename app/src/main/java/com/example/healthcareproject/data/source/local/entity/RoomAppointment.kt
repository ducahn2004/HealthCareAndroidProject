package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.healthcareproject.data.source.local.Converters
import java.time.LocalDateTime

@Entity(
    tableName = "appointments",
    foreignKeys = [
        ForeignKey(
            entity = RoomUser::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoomMedicalVisit::class,
            parentColumns = ["visitId"],
            childColumns = ["visitId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("userId"), Index("visitId")]
)
@TypeConverters(Converters::class)
data class RoomAppointment(
    @PrimaryKey val appointmentId: String,
    val userId: String,
    val visitId: String?,
    val doctorName: String,
    val location: String,
    val appointmentTime: LocalDateTime,
    val note: String
)
