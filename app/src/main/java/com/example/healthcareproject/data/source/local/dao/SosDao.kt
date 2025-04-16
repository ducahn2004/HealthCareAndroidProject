package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.Sos
import kotlinx.coroutines.flow.Flow

@Dao
interface SosDao {

    @Query("SELECT * FROM sos")
    fun observeAll(): Flow<List<Sos>>

    @Query("SELECT * FROM sos WHERE sosId = :sosId")
    fun observeById(sosId: String): Flow<Sos>

    @Query("SELECT * FROM sos WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<Sos>>

    @Query("SELECT * FROM sos")
    suspend fun getAll(): List<Sos>

    @Query("SELECT * FROM sos WHERE sosId = :sosId")
    suspend fun getById(sosId: String): Sos?

    @Query("SELECT * FROM sos WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<Sos>

    @Upsert
    suspend fun upsert(sos: Sos)

    @Upsert
    suspend fun upsertAll(sosList: List<Sos>)

    @Query("DELETE FROM sos WHERE sosId = :sosId")
    suspend fun deleteById(sosId: String): Int

    @Query("DELETE FROM sos WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM sos")
    suspend fun deleteAll()
}