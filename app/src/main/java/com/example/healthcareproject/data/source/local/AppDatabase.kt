package com.example.healthcareproject.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.healthcareproject.data.source.local.dao.*
import com.example.healthcareproject.data.source.local.entity.*

@Database(
    entities = [
        RoomAlert::class,
        RoomAppointment::class,
        RoomEmergencyInfo::class,
        RoomMeasurement::class,
        RoomMedicalVisit::class,
        RoomMedication::class,
        RoomNotification::class,
        RoomSos::class,
        RoomUser::class
    ],
    version = 10,
    exportSchema = false
)
@TypeConverters(Converters::class)
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