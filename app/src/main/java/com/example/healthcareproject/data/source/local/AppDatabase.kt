package com.example.healthcareproject.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.healthcareproject.data.source.local.dao.*
import com.example.healthcareproject.data.source.local.entity.*

@Database(
    entities = [
        Alert::class,
        Appointment::class,
        EmergencyInfo::class,
        Measurement::class,
        MedicalVisit::class,
        Medication::class,
        Notification::class,
        Sos::class,
        User::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alertDao(): AlertDao

    abstract fun appointmentDao(): AppointmentDao

    abstract fun emergencyInfoDao(): EmergencyInfoDao

    abstract fun measurementDao(): MeasurementDao

    abstract fun medicalVisitDao(): MedicalVisitDao

    abstract fun medicationDao(): MedicationDao

    abstract fun notificationDao(): NotificationDao

    abstract fun sosDao(): SosDao

    abstract fun userDao(): UserDao
}