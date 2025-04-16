package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.Measurement
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {

    @Query("SELECT * FROM measurement")
    fun observeAll(): Flow<List<Measurement>>

    @Query("SELECT * FROM measurement WHERE measurementId = :measurementId")
    fun observeById(measurementId: String): Flow<Measurement>

    @Query("SELECT * FROM measurement WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<Measurement>>

    @Query("SELECT * FROM measurement")
    suspend fun getAll(): List<Measurement>

    @Query("SELECT * FROM measurement WHERE measurementId = :measurementId")
    suspend fun getById(measurementId: String): Measurement?

    @Query("SELECT * FROM measurement WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<Measurement>

    @Upsert
    suspend fun upsert(measurement: Measurement)

    @Upsert
    suspend fun upsertAll(measurements: List<Measurement>)

    @Query("DELETE FROM measurement WHERE measurementId = :measurementId")
    suspend fun deleteById(measurementId: String): Int

    @Query("DELETE FROM measurement WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM measurement")
    suspend fun deleteAll()
}