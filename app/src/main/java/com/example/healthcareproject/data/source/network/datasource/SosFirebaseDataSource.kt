package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseSos
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SosFirebaseDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : SosDataSource {

    private val sosRef = firebaseDatabase.getReference("sos")

    override suspend fun loadSos(userId: String): List<FirebaseSos> = try {
        sosRef
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .await()
            .children
            .mapNotNull { it.getValue(FirebaseSos::class.java) }
    } catch (e: Exception) {
        throw Exception("Error loading SOS data for userId '$userId': ${e.message}", e)
    }

    override suspend fun saveSos(sosList: List<FirebaseSos>) {
        if (sosList.isEmpty()) return

        try {
            val updates = sosList.associateBy { it.sosId }
            sosRef.updateChildren(updates).await()
        } catch (e: Exception) {
            throw Exception("Error saving SOS data: ${e.message}", e)
        }
    }
}