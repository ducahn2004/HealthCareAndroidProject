package com.example.healthcareproject.data.source.network.model

data class FirebaseMedicalVisit(
    var id: String = "",
    var userId: String = "",
    var doctorName: String = "",
    var location: String = "",
    var visitDate: String = "",
    var notes: String = ""
)

