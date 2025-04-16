package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "appointments",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MedicalVisit::class,
            parentColumns = ["visitId"],
            childColumns = ["visitId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("userId"), Index("visitId")]
)
data class Appointment(
    @PrimaryKey val appointmentId: String,
    val userId: String,
    val visitId: String?,
    val doctorName: String,
    val location: String,
    val appointmentTime: LocalDateTime,
    val note: String
)
