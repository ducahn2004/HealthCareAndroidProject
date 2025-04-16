package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.source.local.entity.MedicalVisit
import com.example.healthcareproject.data.source.network.datasource.MedicalVisitDataSource
import kotlinx.coroutines.flow.Flow

class MedicalVisitRepository(private val medicalVisitDataSource: MedicalVisitDataSource) {

    fun observeAll(): Flow<List<MedicalVisit>> = medicalVisitDataSource.observeAll()

    fun observeById(medicalVisitId: String): Flow<MedicalVisit?> = medicalVisitDataSource.observeById(medicalVisitId)

    suspend fun getAll(): List<MedicalVisit> = medicalVisitDataSource.getAll()

    suspend fun getById(medicalVisitId: String): MedicalVisit? = medicalVisitDataSource.getById(medicalVisitId)

    suspend fun upsert(medicalVisit: MedicalVisit) = medicalVisitDataSource.upsert(medicalVisit)

    suspend fun upsertAll(medicalVisits: List<MedicalVisit>) = medicalVisitDataSource.upsertAll(medicalVisits)

    suspend fun deleteById(medicalVisitId: String): Int = medicalVisitDataSource.deleteById(medicalVisitId)

    suspend fun deleteAll(): Int = medicalVisitDataSource.deleteAll()
}