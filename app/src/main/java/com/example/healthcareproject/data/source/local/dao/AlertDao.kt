package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.healthcareproject.data.source.local.entity.RoomAlert

@Dao
interface AlertDao {

    @Update
    suspend fun upsert(alert: RoomAlert)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(alerts: List<RoomAlert>)

    @Query("DELETE FROM alerts WHERE alertId = :alertId")
    suspend fun deleteById(alertId: String)

    @Query("UPDATE alerts SET status = :status WHERE alertId = :alertId")
    suspend fun updateStatus(alertId: String, status: Boolean)

    @Query("DELETE FROM alerts WHERE status = 0")
    suspend fun deleteInactive()

    @Query("DELETE FROM alerts")
    suspend fun deleteAll()

    @Query("SELECT * FROM alerts WHERE alertId = :alertId")
    suspend fun getById(alertId: String): RoomAlert?

    @Query("SELECT * FROM alerts")
    suspend fun getAll(): List<RoomAlert>

    @Query("SELECT * FROM alerts")
    fun observeAll(): kotlinx.coroutines.flow.Flow<List<RoomAlert>>

    @Query("SELECT * FROM alerts WHERE alertId = :alertId")
    fun observeById(alertId: String): kotlinx.coroutines.flow.Flow<RoomAlert?>
}