package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.RoomReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders")
    fun observeAll(): Flow<List<RoomReminder>>

    @Query("SELECT * FROM reminders WHERE reminderId = :reminderId")
    fun observeById(reminderId: String): Flow<RoomReminder>

    @Query("SELECT * FROM reminders WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<RoomReminder>>

    @Query("SELECT * FROM reminders")
    suspend fun getAll(): List<RoomReminder>

    @Query("SELECT * FROM reminders WHERE reminderId = :reminderId")
    suspend fun getById(reminderId: String): RoomReminder?

    @Query("SELECT * FROM reminders WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<RoomReminder>

    @Query("DELETE FROM reminders WHERE status = 0")
    suspend fun deleteInactive()

    @Upsert
    suspend fun upsert(reminder: RoomReminder)

    @Upsert
    suspend fun upsertAll(reminders: List<RoomReminder>)

    @Query("DELETE FROM reminders WHERE reminderId = :reminderId")
    suspend fun deleteById(reminderId: String): Int

    @Query("DELETE FROM reminders WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM reminders")
    suspend fun deleteAll()
}