package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.Medication
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Query("SELECT * FROM medications")
    fun observeAll(): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE medicationId = :medicationId")
    fun observeById(medicationId: String): Flow<Medication>

    @Query("SELECT * FROM medications WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE visitId = :visitId")
    fun observeByVisitId(visitId: String): Flow<List<Medication>>

    @Query("SELECT * FROM medications")
    suspend fun getAll(): List<Medication>

    @Query("SELECT * FROM medications WHERE medicationId = :medicationId")
    suspend fun getById(medicationId: String): Medication?

    @Query("SELECT * FROM medications WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<Medication>

    @Query("SELECT * FROM medications WHERE visitId = :visitId")
    suspend fun getByVisitId(visitId: String): List<Medication>

    @Upsert
    suspend fun upsert(medication: Medication)

    @Upsert
    suspend fun upsertAll(medications: List<Medication>)

    @Query("DELETE FROM medications WHERE medicationId = :medicationId")
    suspend fun deleteById(medicationId: String): Int

    @Query("DELETE FROM medications WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM medications WHERE visitId = :visitId")
    suspend fun deleteByVisitId(visitId: String): Int

    @Query("DELETE FROM medications")
    suspend fun deleteAll()
}