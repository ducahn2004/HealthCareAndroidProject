package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.healthcareproject.data.source.local.Converters
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import java.time.LocalDate

@Entity(
    tableName = "medications",
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
data class RoomMedication(
    @PrimaryKey val medicationId: String,
    val userId: String,
    val visitId: String?,
    val name: String,
    val dosageUnit: DosageUnit,
    val dosageAmount: Float,
    val frequency: Int,
    val timeOfDay: List<String>,
    val mealRelation: MealRelation,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val notes: String
)

