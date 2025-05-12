package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.RoomMedicalVisit
import com.example.healthcareproject.data.source.local.entity.RoomMedication
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Query("SELECT * FROM medications")
    fun observeAll(): Flow<List<RoomMedication>>

    @Query("SELECT * FROM medications WHERE medicationId = :medicationId")
    fun observeById(medicationId: String): Flow<RoomMedication>

    @Query("SELECT * FROM medications WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<RoomMedication>>

    @Query("SELECT * FROM medications WHERE visitId = :visitId")
    fun observeByVisitId(visitId: String): Flow<List<RoomMedication>>

    @Query("SELECT * FROM medications")
    suspend fun getAll(): List<RoomMedication>

    @Query("SELECT * FROM medications WHERE medicationId = :medicationId")
    suspend fun getById(medicationId: String): RoomMedication?

    @Query("SELECT * FROM medical_visits WHERE visitId = :visitId LIMIT 1")
    suspend fun getMedicalVisitById(visitId: String?): RoomMedicalVisit?

    @Query("SELECT * FROM medications WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<RoomMedication>

    @Query("SELECT * FROM medications WHERE visitId = :visitId")
    suspend fun getByVisitId(visitId: String): List<RoomMedication>

    @Upsert
    suspend fun upsert(medication: RoomMedication)

    @Upsert
    suspend fun upsertAll(medications: List<RoomMedication>)

    @Query("DELETE FROM medications WHERE medicationId = :medicationId")
    suspend fun deleteById(medicationId: String): Int

    @Query("DELETE FROM medications WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM medications WHERE visitId = :visitId")
    suspend fun deleteByVisitId(visitId: String): Int

    @Query("DELETE FROM medications")
    suspend fun deleteAll()
}
