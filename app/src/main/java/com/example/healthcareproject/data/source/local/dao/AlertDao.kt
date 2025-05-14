package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.RoomAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Query("SELECT * FROM alert")
    fun observeAll(): Flow<List<RoomAlert>>

    @Query("SELECT * FROM alert WHERE alertId = :alertId")
    fun observeById(alertId: String): Flow<RoomAlert>

    @Query("SELECT * FROM alert WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<RoomAlert>>

    @Query("SELECT * FROM alert")
    suspend fun getAll(): List<RoomAlert>

    @Query("SELECT * FROM alert WHERE alertId = :alertId")
    suspend fun getById(alertId: String): RoomAlert?

    @Query("SELECT * FROM alert WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<RoomAlert>

    @Upsert
    suspend fun upsert(alert: RoomAlert)

    @Upsert
    suspend fun upsertAll(alertList: List<RoomAlert>)

    @Query("DELETE FROM alert WHERE alertId = :alertId")
    suspend fun deleteById(alertId: String): Int

    @Query("DELETE FROM alert WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM alert")
    suspend fun deleteAll()
}