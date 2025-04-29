package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseMedicalVisit
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MedicalVisitFirebaseDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : MedicalVisitDataSource {

    private val medicalVisitRef = firebaseDatabase.getReference("medical_visits")

    override suspend fun loadMedicalVisits(userId: String): List<FirebaseMedicalVisit> = try {
        medicalVisitRef
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .await()
            .children
            .mapNotNull { it.getValue(FirebaseMedicalVisit::class.java) }
    } catch (e: Exception) {
        throw Exception("Error loading medical visits for userId '$userId': ${e.message}", e)
    }

    override suspend fun saveMedicalVisits(medicalVisits: List<FirebaseMedicalVisit>) {
        if (medicalVisits.isEmpty()) return

        try {
            val updates = medicalVisits.associateBy { it.medicalVisitId }
            medicalVisitRef.updateChildren(updates).await()
        } catch (e: Exception) {
            throw Exception("Error saving medical visits: ${e.message}", e)
        }
    }
}