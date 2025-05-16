package com.example.healthcareproject.di

import com.example.healthcareproject.data.repository.*
import com.example.healthcareproject.domain.repository.ReminderRepository
import com.example.healthcareproject.domain.repository.AppointmentRepository
import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import com.example.healthcareproject.domain.repository.MeasurementRepository
import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import com.example.healthcareproject.domain.repository.MedicationRepository
import com.example.healthcareproject.domain.repository.NotificationRepository
import com.example.healthcareproject.domain.repository.AlertRepository
import com.example.healthcareproject.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindUserRepository(repository: DefaultUserRepository): UserRepository

    @Singleton
    @Binds
    abstract fun bindReminderRepository(repository: DefaultReminderRepository): ReminderRepository

    @Singleton
    @Binds
    abstract fun bindAppointmentRepository(repository: DefaultAppointmentRepository): AppointmentRepository

    @Singleton
    @Binds
    abstract fun bindEmergencyInfoRepository(repository: DefaultEmergencyInfoRepository): EmergencyInfoRepository

    @Singleton
    @Binds
    abstract fun bindMeasurementRepository(repository: DefaultMeasurementRepository): MeasurementRepository

    @Singleton
    @Binds
    abstract fun bindMedicalVisitRepository(repository: DefaultMedicalVisitRepository): MedicalVisitRepository

    @Singleton
    @Binds
    abstract fun bindMedicationRepository(repository: DefaultMedicationRepository): MedicationRepository

    @Singleton
    @Binds
    abstract fun bindNotificationRepository(repository: DefaultNotificationRepository): NotificationRepository

    @Singleton
    @Binds
    abstract fun bindAlertRepository(repository: DefaultAlertRepository): AlertRepository
}