package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.User
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    fun observeAll(): Flow<List<User>>

    fun observeById(userId: String): Flow<User?>

    suspend fun getAll(): List<User>

    suspend fun getById(userId: String): User?

    suspend fun upsert(user: User)

    suspend fun upsertAll(users: List<User>)

    suspend fun deleteById(userId: String): Int

    suspend fun deleteAll(): Int
}