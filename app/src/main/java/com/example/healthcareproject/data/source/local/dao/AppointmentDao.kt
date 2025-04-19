package com.example.healthcareproject.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.healthcareproject.data.source.local.entity.Appointment
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    @Query("SELECT * FROM appointments")
    fun observeAll(): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE appointmentId = :appointmentId")
    fun observeById(appointmentId: String): Flow<Appointment>

    @Query("SELECT * FROM appointments WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments")
    suspend fun getAll(): List<Appointment>

    @Query("SELECT * FROM appointments WHERE appointmentId = :appointmentId")
    suspend fun getById(appointmentId: String): Appointment?

    @Query("SELECT * FROM appointments WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<Appointment>

    @Upsert
    suspend fun upsert(appointment: Appointment)

    @Upsert
    suspend fun upsertAll(appointments: List<Appointment>)

    @Query("DELETE FROM appointments WHERE appointmentId = :appointmentId")
    suspend fun deleteById(appointmentId: String): Int

    @Query("DELETE FROM appointments WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM appointments")
    suspend fun deleteAll()
}