package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "medications",
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
data class Medication(
    @PrimaryKey val medicationId: String,
    val userId: String,
    val visitId: String?,
    val name: String,
    val dosageUnit: String,
    val dosageAmount: String,
    val frequency: String,
    val timeOfDay: String,
    val mealRelation: MealRelation,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val notes: String
)

enum class MealRelation {
    BeforeMeal,
    AfterMeal,
    WithMeal
}
