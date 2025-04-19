package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseMedication

interface MedicationDataSource {

    suspend fun writeMedication(medication: FirebaseMedication)

    suspend fun readMedication(medicationId: String): FirebaseMedication?

    suspend fun deleteMedication(medicationId: String)

    suspend fun updateMedication(medicationId: String, medication: FirebaseMedication)

    suspend fun readAllMedicationsByUserId(userId: String): List<FirebaseMedication>?
}