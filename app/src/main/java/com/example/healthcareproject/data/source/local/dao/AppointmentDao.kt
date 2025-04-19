package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.RoomAppointment
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    @Query("SELECT * FROM appointments")
    fun observeAll(): Flow<List<RoomAppointment>>

    @Query("SELECT * FROM appointments WHERE appointmentId = :appointmentId")
    fun observeById(appointmentId: String): Flow<RoomAppointment>

    @Query("SELECT * FROM appointments WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<RoomAppointment>>

    @Query("SELECT * FROM appointments")
    suspend fun getAll(): List<RoomAppointment>

    @Query("SELECT * FROM appointments WHERE appointmentId = :appointmentId")
    suspend fun getById(appointmentId: String): RoomAppointment?

    @Query("SELECT * FROM appointments WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<RoomAppointment>

    @Upsert
    suspend fun upsert(appointment: RoomAppointment)

    @Upsert
    suspend fun upsertAll(appointments: List<RoomAppointment>)

    @Query("DELETE FROM appointments WHERE appointmentId = :appointmentId")
    suspend fun deleteById(appointmentId: String): Int

    @Query("DELETE FROM appointments WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM appointments")
    suspend fun deleteAll()
}