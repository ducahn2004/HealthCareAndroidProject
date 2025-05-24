package com.example.healthcareproject.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.healthcareproject.data.source.local.dao.*
import com.example.healthcareproject.data.source.local.entity.*

@Database(
    entities = [
        RoomReminder::class,
        RoomAppointment::class,
        RoomEmergencyInfo::class,
        RoomMeasurement::class,
        RoomMedicalVisit::class,
        RoomMedication::class,
        RoomNotification::class,
        RoomAlert::class,
        RoomUser::class
    ],
    version = 21,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    abstract fun appointmentDao(): AppointmentDao

    abstract fun emergencyInfoDao(): EmergencyInfoDao

    abstract fun measurementDao(): MeasurementDao

    abstract fun medicalVisitDao(): MedicalVisitDao

    abstract fun medicationDao(): MedicationDao

    abstract fun notificationDao(): NotificationDao

    abstract fun alertDao(): AlertDao

    abstract fun userDao(): UserDao
}