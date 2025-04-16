package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.Alert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Query("SELECT * FROM alerts")
    fun observeAll(): Flow<List<Alert>>

    @Query("SELECT * FROM alerts WHERE alertId = :alertId")
    fun observeById(alertId: String): Flow<Alert>

    @Query("SELECT * FROM alerts WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<Alert>>

    @Query("SELECT * FROM alerts")
    suspend fun getAll(): List<Alert>

    @Query("SELECT * FROM alerts WHERE alertId = :alertId")
    suspend fun getById(alertId: String): Alert?

    @Query("SELECT * FROM alerts WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<Alert>

    @Upsert
    suspend fun upsert(alert: Alert)

    @Upsert
    suspend fun upsertAll(alerts: List<Alert>)

    @Query("DELETE FROM alerts WHERE alertId = :alertId")
    suspend fun deleteById(alertId: String): Int

    @Query("DELETE FROM alerts WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM alerts")
    suspend fun deleteAll()
}