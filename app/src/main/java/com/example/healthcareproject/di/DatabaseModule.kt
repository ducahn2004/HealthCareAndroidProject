package com.example.healthcareproject.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.healthcareproject.data.source.local.AppDatabase
import com.example.healthcareproject.data.source.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "AppDatabase.db"
        )
            .fallbackToDestructiveMigration(false)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    db.execSQL("PRAGMA foreign_keys=ON;")
                }
            })
            .build()
    }

    @Provides
    fun provideReminderDao(database: AppDatabase): ReminderDao = database.reminderDao()

    @Provides
    fun provideAppointmentDao(database: AppDatabase): AppointmentDao = database.appointmentDao()

    @Provides
    fun provideEmergencyInfoDao(database: AppDatabase): EmergencyInfoDao = database.emergencyInfoDao()

    @Provides
    fun provideMeasurementDao(database: AppDatabase): MeasurementDao = database.measurementDao()

    @Provides
    fun provideMedicalVisitDao(database: AppDatabase): MedicalVisitDao = database.medicalVisitDao()

    @Provides
    fun provideMedicationDao(database: AppDatabase): MedicationDao = database.medicationDao()

    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDao = database.notificationDao()

    @Provides
    fun provideSosDao(database: AppDatabase): SosDao = database.sosDao()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
}