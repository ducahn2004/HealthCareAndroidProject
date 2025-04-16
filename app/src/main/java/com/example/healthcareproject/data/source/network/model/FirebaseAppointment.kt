package com.example.healthcareproject.data.source.network.model

data class FirebaseAppointment(
    var id: String = "",
    var userId: String = "",
    var doctorName: String = "",
    var appointmentTime: String = "",
    var purpose: String = ""
)

