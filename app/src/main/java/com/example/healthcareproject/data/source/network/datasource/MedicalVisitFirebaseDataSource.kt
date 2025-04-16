package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.MedicalVisit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MedicalVisitFirebaseDataSource : MedicalVisitDataSource {

    override fun observeAll(): Flow<List<MedicalVisit>> = flowOf(emptyList())

    override fun observeById(medicalVisitId: String): Flow<MedicalVisit?> = flowOf(null)

    override suspend fun getAll(): List<MedicalVisit> = emptyList()

    override suspend fun getById(medicalVisitId: String): MedicalVisit? = null

    override suspend fun upsert(medicalVisit: MedicalVisit) {}

    override suspend fun upsertAll(medicalVisits: List<MedicalVisit>) {}

    override suspend fun deleteById(medicalVisitId: String): Int = 0

    override suspend fun deleteAll(): Int = 0
}