package com.example.healthcareproject.data.source.network.model

data class FirebaseAlert(
    var id: String = "",
    var userId: String = "",
    var title: String = "",
    var description: String = "",
    var alertTime: String = "",
    var isActive: Boolean = true
)


