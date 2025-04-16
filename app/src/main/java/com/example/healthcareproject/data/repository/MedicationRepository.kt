package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.source.local.entity.Medication
import com.example.healthcareproject.data.source.network.datasource.MedicationDataSource
import kotlinx.coroutines.flow.Flow

class MedicationRepository(private val medicationDataSource: MedicationDataSource) {

    fun observeAll(): Flow<List<Medication>> = medicationDataSource.observeAll()

    fun observeById(medicationId: String): Flow<Medication?> = medicationDataSource.observeById(medicationId)

    suspend fun getAll(): List<Medication> = medicationDataSource.getAll()

    suspend fun getById(medicationId: String): Medication? = medicationDataSource.getById(medicationId)

    suspend fun upsert(medication: Medication) = medicationDataSource.upsert(medication)

    suspend fun upsertAll(medications: List<Medication>) = medicationDataSource.upsertAll(medications)

    suspend fun deleteById(medicationId: String): Int = medicationDataSource.deleteById(medicationId)

    suspend fun deleteAll(): Int = medicationDataSource.deleteAll()
}