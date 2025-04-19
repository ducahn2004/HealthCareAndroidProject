package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.firebase.FirebaseService
import com.example.healthcareproject.data.source.network.model.FirebaseMedication
import kotlinx.coroutines.tasks.await

class MedicationFirebaseDataSource : MedicationDataSource {

    private val medicationRef = FirebaseService.getReference("medications")

    override suspend fun writeMedication(medication: FirebaseMedication) {
        medicationRef.child(medication.medicationId).setValue(medication).await()
    }

    override suspend fun readMedication(medicationId: String): FirebaseMedication? {
        val snapshot = medicationRef.child(medicationId).get().await()
        return snapshot.getValue(FirebaseMedication::class.java)
    }

    override suspend fun deleteMedication(medicationId: String) {
        medicationRef.child(medicationId).removeValue().await()
    }

    override suspend fun updateMedication(medicationId: String, medication: FirebaseMedication) {
        medicationRef.child(medicationId).setValue(medication).await()
    }

    override suspend fun readAllMedicationsByUserId(userId: String): List<FirebaseMedication> {
        val snapshot = medicationRef.orderByChild("userId").equalTo(userId).get().await()
        return snapshot.children.mapNotNull { it.getValue(FirebaseMedication::class.java) }
    }
}

