package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseMedication

interface MedicationDataSource {

    suspend fun loadMedications(userId: String): List<FirebaseMedication>

    suspend fun saveMedications(medications: List<FirebaseMedication>)

    suspend fun deleteMedication(medicationId: String)

    fun removeListeners()

    fun addSyncListener(userId: String, onDataChange: (List<FirebaseMedication>) -> Unit)
}