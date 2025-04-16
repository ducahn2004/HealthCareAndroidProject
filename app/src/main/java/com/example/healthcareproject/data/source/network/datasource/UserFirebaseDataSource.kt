package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UserFirebaseDataSource : UserDataSource {

    override fun observeAll(): Flow<List<User>> = flowOf(emptyList())

    override fun observeById(userId: String): Flow<User?> = flowOf(null)

    override suspend fun getAll(): List<User> = emptyList()

    override suspend fun getById(userId: String): User? = null

    override suspend fun upsert(user: User) {}

    override suspend fun upsertAll(users: List<User>) {}

    override suspend fun deleteById(userId: String): Int = 0

    override suspend fun deleteAll(): Int = 0
}