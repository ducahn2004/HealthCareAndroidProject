package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.Medication
import kotlinx.coroutines.flow.Flow

interface MedicationDataSource {
    fun observeAll(): Flow<List<Medication>>

    fun observeById(medicationId: String): Flow<Medication?>

    suspend fun getAll(): List<Medication>

    suspend fun getById(medicationId: String): Medication?

    suspend fun upsert(medication: Medication)

    suspend fun upsertAll(medications: List<Medication>)

    suspend fun deleteById(medicationId: String): Int

    suspend fun deleteAll(): Int
}