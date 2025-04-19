package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.Sos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SosFirebaseDataSource : SosDataSource {

    override fun observeAll(): Flow<List<Sos>> = flowOf(emptyList())

    override fun observeById(sosId: String): Flow<Sos?> = flowOf(null)

    override fun observeByUserId(userId: String): Flow<List<Sos>> = flowOf(emptyList())

    override suspend fun getAll(): List<Sos> = emptyList()

    override suspend fun getById(sosId: String): Sos? = null

    override suspend fun getByUserId(userId: String): List<Sos> = emptyList()

    override suspend fun upsert(sos: Sos) {
        // Add or update the SOS record in Firebase
    }

    override suspend fun upsertAll(sosList: List<Sos>) {
        // Add or update multiple SOS records in Firebase
    }

    override suspend fun deleteById(sosId: String): Int {
        // Delete the SOS record by ID in Firebase
        return 0
    }

    override suspend fun deleteByUserId(userId: String): Int {
        // Delete SOS records by user ID in Firebase
        return 0
    }

    override suspend fun deleteAll(): Int {
        // Delete all SOS records in Firebase
        return 0
    }
}