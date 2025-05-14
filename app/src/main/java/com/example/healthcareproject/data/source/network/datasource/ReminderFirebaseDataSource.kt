package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseReminder
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReminderFirebaseDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : ReminderDataSource {

    private val remindersRef = firebaseDatabase.getReference("reminders")

    override suspend fun loadReminders(userId: String): List<FirebaseReminder> = try {
        remindersRef
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .await()
            .children
            .mapNotNull { it.getValue(FirebaseReminder::class.java) }
    } catch (e: Exception) {
        throw Exception("Error loading reminders for userId '$userId': ${e.message}", e)
    }

    override suspend fun saveReminders(reminders: List<FirebaseReminder>) {
        if (reminders.isEmpty()) return

        try {
            val updates = reminders.associateBy { it.reminderId }
            remindersRef.updateChildren(updates).await()
        } catch (e: Exception) {
            throw Exception("Error saving reminders: ${e.message}", e)
        }
    }
}