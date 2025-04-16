package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.EmergencyInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyInfoDao {

    @Query("SELECT * FROM emergency_info")
    fun observeAll(): Flow<List<EmergencyInfo>>

    @Query("SELECT * FROM emergency_info WHERE emergencyId = :emergencyId")
    fun observeById(emergencyId: String): Flow<EmergencyInfo>

    @Query("SELECT * FROM emergency_info WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<EmergencyInfo>>

    @Query("SELECT * FROM emergency_info")
    suspend fun getAll(): List<EmergencyInfo>

    @Query("SELECT * FROM emergency_info WHERE emergencyId = :emergencyId")
    suspend fun getById(emergencyId: String): EmergencyInfo?

    @Query("SELECT * FROM emergency_info WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<EmergencyInfo>

    @Upsert
    suspend fun upsert(emergencyInfo: EmergencyInfo)

    @Upsert
    suspend fun upsertAll(emergencyInfos: List<EmergencyInfo>)

    @Query("DELETE FROM emergency_info WHERE emergencyId = :emergencyId")
    suspend fun deleteById(emergencyId: String): Int

    @Query("DELETE FROM emergency_info WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM emergency_info")
    suspend fun deleteAll()
}