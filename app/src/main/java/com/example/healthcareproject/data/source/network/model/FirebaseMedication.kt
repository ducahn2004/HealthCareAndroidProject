package com.example.healthcareproject.data.source.network.model

data class FirebaseMedication(
    var id: String = "",
    var userId: String = "",
    var name: String = "",
    var dosage: String = "",
    var frequency: String = "",
    var timeOfDay: List<String> = emptyList(),
    var beforeAfterMeal: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var notes: String = ""
)

