package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.source.local.entity.User
import com.example.healthcareproject.data.source.network.datasource.UserDataSource
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDataSource: UserDataSource) {

    fun observeAll(): Flow<List<User>> = userDataSource.observeAll()

    fun observeById(userId: String): Flow<User?> = userDataSource.observeById(userId)

    suspend fun getAll(): List<User> = userDataSource.getAll()

    suspend fun getById(userId: String): User? = userDataSource.getById(userId)

    suspend fun upsert(user: User) = userDataSource.upsert(user)

    suspend fun upsertAll(users: List<User>) = userDataSource.upsertAll(users)

    suspend fun deleteById(userId: String): Int = userDataSource.deleteById(userId)

    suspend fun deleteAll(): Int = userDataSource.deleteAll()
}