package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.MedicalVisit
import kotlinx.coroutines.flow.Flow

interface MedicalVisitDataSource {
    fun observeAll(): Flow<List<MedicalVisit>>

    fun observeById(medicalVisitId: String): Flow<MedicalVisit?>

    suspend fun getAll(): List<MedicalVisit>

    suspend fun getById(medicalVisitId: String): MedicalVisit?

    suspend fun upsert(medicalVisit: MedicalVisit)

    suspend fun upsertAll(medicalVisits: List<MedicalVisit>)

    suspend fun deleteById(medicalVisitId: String): Int

    suspend fun deleteAll(): Int
}