package com.example.healthcareproject.data.source.network.model

import com.example.healthcareproject.domain.model.RepeatPattern

data class FirebaseReminder(
    var reminderId: String = "",
    var userId: String = "",
    var title: String = "",
    var message: String = "",
    var reminderTime: String = "",
    var repeatPattern: RepeatPattern = RepeatPattern.None,
    var status: Boolean,
    var createdAt: String = "",
    var startDate: String = "",
    var endDate: String = "",
)