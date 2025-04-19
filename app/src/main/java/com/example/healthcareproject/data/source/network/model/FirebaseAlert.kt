package com.example.healthcareproject.data.source.network.model

import com.example.healthcareproject.domain.model.RepeatPattern

data class FirebaseAlert(
    var alertId: String = "",
    var userId: String = "",
    var title: String = "",
    var message: String = "",
    var alertTime: String = "",
    var repeatPattern: RepeatPattern = RepeatPattern.None,
    var status: Boolean,
    var createdAt: String = ""
)



