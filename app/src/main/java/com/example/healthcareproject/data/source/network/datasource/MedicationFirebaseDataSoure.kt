package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.Medication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MedicationFirebaseDataSource : MedicationDataSource {

    override fun observeAll(): Flow<List<Medication>> = flowOf(emptyList())

    override fun observeById(medicationId: String): Flow<Medication?> = flowOf(null)

    override suspend fun getAll(): List<Medication> = emptyList()

    override suspend fun getById(medicationId: String): Medication? = null

    override suspend fun upsert(medication: Medication) {}

    override suspend fun upsertAll(medications: List<Medication>) {}

    override suspend fun deleteById(medicationId: String): Int = 0

    override suspend fun deleteAll(): Int = 0
}

