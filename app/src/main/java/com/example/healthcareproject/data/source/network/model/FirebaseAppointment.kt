package com.example.healthcareproject.data.source.network.model

data class FirebaseAppointment(
    var appointmentId: String = "",
    var userId: String = "",
    var visitId: String? = null,
    var doctorName: String = "",
    var location: String,
    var appointmentTime: String = "",
    var note: String = ""
)

