package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.firebase.FirebaseService
import com.example.healthcareproject.data.source.network.model.FirebaseSos
import kotlinx.coroutines.tasks.await

class SosFirebaseDataSource : SosDataSource {

    private val sosRef = FirebaseService.getReference("sos")

    override suspend fun writeSos(sos: FirebaseSos) {
        sosRef.child(sos.sosId).setValue(sos).await()
    }

    override suspend fun readSos(sosId: String): FirebaseSos? {
        val snapshot = sosRef.child(sosId).get().await()
        return snapshot.getValue(FirebaseSos::class.java)
    }

    override suspend fun deleteSos(sosId: String) {
        sosRef.child(sosId).removeValue().await()
    }

    override suspend fun updateSos(sosId: String, sos: FirebaseSos) {
        sosRef.child(sosId).setValue(sos).await()
    }

    override suspend fun readAllSosByUserId(userId: String): List<FirebaseSos> {
        val snapshot = sosRef.orderByChild("userId").equalTo(userId).get().await()
        return snapshot.children.mapNotNull { it.getValue(FirebaseSos::class.java) }
    }
}