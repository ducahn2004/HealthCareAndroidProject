package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.healthcareproject.data.source.local.entity.RoomReminder

@Dao
interface ReminderDao {

    @Update
    suspend fun upsert(reminder: RoomReminder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(reminders: List<RoomReminder>)

    @Query("DELETE FROM reminders WHERE reminderId = :reminderId")
    suspend fun deleteById(reminderId: String)

    @Query("UPDATE reminders SET status = :status WHERE reminderId = :reminderId")
    suspend fun updateStatus(reminderId: String, status: Boolean)

    @Query("DELETE FROM reminders WHERE status = 0")
    suspend fun deleteInactive()

    @Query("DELETE FROM reminders")
    suspend fun deleteAll()

    @Query("SELECT * FROM reminders WHERE reminderId = :reminderId")
    suspend fun getById(reminderId: String): RoomReminder?

    @Query("SELECT * FROM reminders")
    suspend fun getAll(): List<RoomReminder>

    @Query("SELECT * FROM reminders")
    fun observeAll(): kotlinx.coroutines.flow.Flow<List<RoomReminder>>

    @Query("SELECT * FROM reminders WHERE reminderId = :reminderId")
    fun observeById(reminderId: String): kotlinx.coroutines.flow.Flow<RoomReminder?>
}