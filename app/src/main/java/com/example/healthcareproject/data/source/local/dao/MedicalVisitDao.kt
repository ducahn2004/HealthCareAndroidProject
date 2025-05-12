package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.RoomMedicalVisit
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalVisitDao {

    @Query("SELECT * FROM medical_visits")
    fun observeAll(): Flow<List<RoomMedicalVisit>>

    @Query("SELECT * FROM medical_visits WHERE visitId = :visitId")
    fun observeById(visitId: String): Flow<RoomMedicalVisit>

    @Query("SELECT * FROM medical_visits WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<RoomMedicalVisit>>

    @Query("SELECT * FROM medical_visits")
    suspend fun getAll(): List<RoomMedicalVisit>

    @Query("SELECT * FROM medical_visits WHERE visitId = :visitId")
    suspend fun getById(visitId: String): RoomMedicalVisit?

    @Query("SELECT * FROM medical_visits WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<RoomMedicalVisit>

    @Query("SELECT * FROM medical_visits WHERE visitId = :visitId LIMIT 1")
    suspend fun getMedicalVisitById(visitId: String?): RoomMedicalVisit?

    @Upsert
    suspend fun upsert(medicalVisit: RoomMedicalVisit)

    @Upsert
    suspend fun upsertAll(medicalVisits: List<RoomMedicalVisit>)

    @Query("DELETE FROM medical_visits WHERE visitId = :visitId")
    suspend fun deleteById(visitId: String): Int

    @Query("DELETE FROM medical_visits WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM medical_visits")
    suspend fun deleteAll()
}