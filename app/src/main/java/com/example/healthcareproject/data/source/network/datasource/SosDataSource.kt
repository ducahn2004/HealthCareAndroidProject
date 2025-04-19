package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.Sos
import kotlinx.coroutines.flow.Flow

interface SosDataSource {
    fun observeAll(): Flow<List<Sos>>

    fun observeById(sosId: String): Flow<Sos?>

    fun observeByUserId(userId: String): Flow<List<Sos>>

    suspend fun getAll(): List<Sos>

    suspend fun getById(sosId: String): Sos?

    suspend fun getByUserId(userId: String): List<Sos>

    suspend fun upsert(sos: Sos)

    suspend fun upsertAll(sosList: List<Sos>)

    suspend fun deleteById(sosId: String): Int

    suspend fun deleteByUserId(userId: String): Int

    suspend fun deleteAll(): Int
}