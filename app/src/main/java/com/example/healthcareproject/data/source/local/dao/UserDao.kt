package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.RoomUser
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun observeById(userId: String): Flow<RoomUser>

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getById(userId: String): RoomUser?

    @Upsert
    suspend fun upsert(user: RoomUser)

    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteById(userId: String): Int
}