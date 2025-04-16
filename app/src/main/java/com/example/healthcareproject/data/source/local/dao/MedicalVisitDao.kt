package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.MedicalVisit
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalVisitDao {

    @Query("SELECT * FROM medical_visits")
    fun observeAll(): Flow<List<MedicalVisit>>

    @Query("SELECT * FROM medical_visits WHERE visitId = :visitId")
    fun observeById(visitId: String): Flow<MedicalVisit>

    @Query("SELECT * FROM medical_visits WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<MedicalVisit>>

    @Query("SELECT * FROM medical_visits")
    suspend fun getAll(): List<MedicalVisit>

    @Query("SELECT * FROM medical_visits WHERE visitId = :visitId")
    suspend fun getById(visitId: String): MedicalVisit?

    @Query("SELECT * FROM medical_visits WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<MedicalVisit>

    @Upsert
    suspend fun upsert(medicalVisit: MedicalVisit)

    @Upsert
    suspend fun upsertAll(medicalVisits: List<MedicalVisit>)

    @Query("DELETE FROM medical_visits WHERE visitId = :visitId")
    suspend fun deleteById(visitId: String): Int

    @Query("DELETE FROM medical_visits WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM medical_visits")
    suspend fun deleteAll()
}