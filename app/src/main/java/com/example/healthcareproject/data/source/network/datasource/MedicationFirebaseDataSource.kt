package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseMedication
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MedicationFirebaseDataSource @Inject constructor(
    firebaseDatabase: FirebaseDatabase
) : MedicationDataSource {

    private val medicationRef = firebaseDatabase.getReference("medications")

    override suspend fun loadMedications(userId: String): List<FirebaseMedication> = try {
        medicationRef
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .await()
            .children
            .mapNotNull { it.getValue(FirebaseMedication::class.java) }
    } catch (e: Exception) {
        throw Exception("Error loading medications for userId '$userId': ${e.message}", e)
    }

    override suspend fun saveMedications(medications: List<FirebaseMedication>) {
        if (medications.isEmpty()) return

        try {
            val updates = medications.associateBy { it.medicationId }
            medicationRef.updateChildren(updates).await()
        } catch (e: Exception) {
            throw Exception("Error saving medications: ${e.message}", e)
        }
    }
}