package com.example.healthcareproject.data.source.network.model

data class FirebaseUser(
    var userId: String = "",
    var password: String = "",
    var name: String = "",
    var address: String? = null,
    var dateOfBirth: String = "",
    var gender: String = "",
    var bloodType: String = "",
    var phone: String = "",
    var createdAt: String = "",
    var updatedAt: String = ""
)

