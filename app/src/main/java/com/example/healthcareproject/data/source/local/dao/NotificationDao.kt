package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications")
    fun observeAll(): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE notificationId = :notificationId")
    fun observeById(notificationId: String): Flow<Notification>

    @Query("SELECT * FROM notifications WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<Notification>>

    @Query("SELECT * FROM notifications")
    suspend fun getAll(): List<Notification>

    @Query("SELECT * FROM notifications WHERE notificationId = :notificationId")
    suspend fun getById(notificationId: String): Notification?

    @Query("SELECT * FROM notifications WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<Notification>

    @Upsert
    suspend fun upsert(notification: Notification)

    @Upsert
    suspend fun upsertAll(notifications: List<Notification>)

    @Query("DELETE FROM notifications WHERE notificationId = :notificationId")
    suspend fun deleteById(notificationId: String): Int

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()
}