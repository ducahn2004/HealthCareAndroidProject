package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseMedicalVisit

interface MedicalVisitDataSource {

    suspend fun writeMedicalVisit(medicalVisit: FirebaseMedicalVisit)

    suspend fun readMedicalVisit(medicalVisitId: String): FirebaseMedicalVisit?

    suspend fun deleteMedicalVisit(medicalVisitId: String)

    suspend fun updateMedicalVisit(medicalVisitId: String, medicalVisit: FirebaseMedicalVisit)

    suspend fun getAllMedicalVisitsByUserId(userId: String): List<FirebaseMedicalVisit>
}