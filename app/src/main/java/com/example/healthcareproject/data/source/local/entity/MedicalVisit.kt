package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "medical_visits",
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
data class MedicalVisit(
    @PrimaryKey val visitId: String,
    val userId: String,
    val visitDate: LocalDate,
    val clinicName: String,
    val doctorName: String,
    val diagnosis: String,
    val treatment: String,
    val createdAt: LocalDateTime
)
