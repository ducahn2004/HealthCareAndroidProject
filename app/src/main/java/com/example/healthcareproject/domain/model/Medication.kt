package com.example.healthcareproject.domain.model

import java.time.LocalDate

data class Medication(
    val medicationId: String,
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