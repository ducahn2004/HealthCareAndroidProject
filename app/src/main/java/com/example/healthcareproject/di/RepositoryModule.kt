package com.example.healthcareproject.di

import com.example.healthcareproject.data.repository.*
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
    abstract fun bindAlertRepository(repository: DefaultAlertRepository): AlertRepository

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
    abstract fun bindSosRepository(repository: DefaultSosRepository): SosRepository
}