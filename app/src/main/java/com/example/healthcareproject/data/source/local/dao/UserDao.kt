package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun observeAll(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun observeById(userId: String): Flow<User>

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getById(userId: String): User?

    @Upsert
    suspend fun upsert(user: User)

    @Upsert
    suspend fun upsertAll(users: List<User>)

    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteById(userId: String): Int

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}