package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseMedicalVisit

interface MedicalVisitDataSource {

    suspend fun loadMedicalVisits(userId: String): List<FirebaseMedicalVisit>

    suspend fun saveMedicalVisits(medicalVisits: List<FirebaseMedicalVisit>)
}