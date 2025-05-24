package com.example.healthcareproject.data.source.network.model

data class FirebaseMedicalVisit(
    var medicalVisitId: String = "",
    var userId: String = "",
    var visitDate: String = "",
    var clinicName: String = "",
    var doctorName: String = "",
    var diagnosis: String = "",
    var treatment: String = "",
    var createdAt: String = ""
)
