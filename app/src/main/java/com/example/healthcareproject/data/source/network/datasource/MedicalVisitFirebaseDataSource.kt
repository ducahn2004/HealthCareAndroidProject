package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.firebase.FirebaseService
import com.example.healthcareproject.data.source.network.model.FirebaseMedicalVisit
import kotlinx.coroutines.tasks.await

class MedicalVisitFirebaseDataSource : MedicalVisitDataSource {

    private val medicalVisitRef = FirebaseService.getReference("medical_visits")

    override suspend fun writeMedicalVisit(medicalVisit: FirebaseMedicalVisit) {
        medicalVisitRef.child(medicalVisit.medicalVisitId).setValue(medicalVisit).await()
    }

    override suspend fun readMedicalVisit(medicalVisitId: String): FirebaseMedicalVisit? {
        val snapshot = medicalVisitRef.child(medicalVisitId).get().await()
        return snapshot.getValue(FirebaseMedicalVisit::class.java)
    }

    override suspend fun deleteMedicalVisit(medicalVisitId: String) {
        medicalVisitRef.child(medicalVisitId).removeValue().await()
    }

    override suspend fun updateMedicalVisit(
        medicalVisitId: String,
        medicalVisit: FirebaseMedicalVisit
    ) {
        medicalVisitRef.child(medicalVisitId).setValue(medicalVisit).await()
    }

    override suspend fun getAllMedicalVisitsByUserId(userId: String): List<FirebaseMedicalVisit> {
        val snapshot = medicalVisitRef.orderByChild("userId").equalTo(userId).get().await()
        return snapshot.children.mapNotNull { it.getValue(FirebaseMedicalVisit::class.java) }
    }
}