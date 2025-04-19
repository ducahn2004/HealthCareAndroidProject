package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.RoomEmergencyInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyInfoDao {

    @Query("SELECT * FROM emergency_infos")
    fun observeAll(): Flow<List<RoomEmergencyInfo>>

    @Query("SELECT * FROM emergency_infos WHERE emergencyId = :emergencyId")
    fun observeById(emergencyId: String): Flow<RoomEmergencyInfo>

    @Query("SELECT * FROM emergency_infos WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<RoomEmergencyInfo>>

    @Query("SELECT * FROM emergency_infos")
    suspend fun getAll(): List<RoomEmergencyInfo>

    @Query("SELECT * FROM emergency_infos WHERE emergencyId = :emergencyId")
    suspend fun getById(emergencyId: String): RoomEmergencyInfo?

    @Query("SELECT * FROM emergency_infos WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<RoomEmergencyInfo>

    @Upsert
    suspend fun upsert(emergencyInfo: RoomEmergencyInfo)

    @Upsert
    suspend fun upsertAll(emergencyInfos: List<RoomEmergencyInfo>)

    @Query("DELETE FROM emergency_infos WHERE emergencyId = :emergencyId")
    suspend fun deleteById(emergencyId: String): Int

    @Query("DELETE FROM emergency_infos WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM emergency_infos")
    suspend fun deleteAll()
}