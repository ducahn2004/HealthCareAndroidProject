package com.example.healthcareproject.data.source.network.model

import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation

data class FirebaseMedication(
    var medicationId: String = "",
    var userId: String = "",
    var visitId: String? = null,
    var name: String = "",
    var dosageUnit: DosageUnit = DosageUnit.None,
    var dosageAmount: Float = 0f,
    var frequency: Int = 0,
    var timeOfDay: List<String> = emptyList(),
    var mealRelation: MealRelation = MealRelation.None,
    var startDate: String = "",
    var endDate: String = "",
    var notes: String = ""
)

