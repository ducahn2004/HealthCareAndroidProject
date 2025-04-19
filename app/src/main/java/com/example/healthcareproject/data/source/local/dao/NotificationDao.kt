package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.RoomNotification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications")
    fun observeAll(): Flow<List<RoomNotification>>

    @Query("SELECT * FROM notifications WHERE notificationId = :notificationId")
    fun observeById(notificationId: String): Flow<RoomNotification>

    @Query("SELECT * FROM notifications WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<RoomNotification>>

    @Query("SELECT * FROM notifications")
    suspend fun getAll(): List<RoomNotification>

    @Query("SELECT * FROM notifications WHERE notificationId = :notificationId")
    suspend fun getById(notificationId: String): RoomNotification?

    @Query("SELECT * FROM notifications WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<RoomNotification>

    @Upsert
    suspend fun upsert(notification: RoomNotification)

    @Upsert
    suspend fun upsertAll(notifications: List<RoomNotification>)

    @Query("DELETE FROM notifications WHERE notificationId = :notificationId")
    suspend fun deleteById(notificationId: String): Int

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()
}