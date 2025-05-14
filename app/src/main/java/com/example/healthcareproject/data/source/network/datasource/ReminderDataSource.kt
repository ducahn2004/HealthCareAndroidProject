package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseReminder

interface ReminderDataSource {

    suspend fun loadReminders(userId: String): List<FirebaseReminder>

    suspend fun saveReminders(reminders: List<FirebaseReminder>)
}