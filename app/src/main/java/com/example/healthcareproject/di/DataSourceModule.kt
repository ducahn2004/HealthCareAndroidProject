package com.example.healthcareproject.di

import com.example.healthcareproject.data.source.network.datasource.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindUserDataSource(dataSource: UserFirebaseDataSource): UserDataSource

    @Singleton
    @Binds
    abstract fun bindSosDataSource(dataSource: SosFirebaseDataSource): SosDataSource

    @Singleton
    @Binds
    abstract fun bindAlertDataSource(dataSource: AlertFirebaseDataSource): AlertDataSource

    @Singleton
    @Binds
    abstract fun bindAppointmentDataSource(dataSource: AppointmentFirebaseDataSource): AppointmentDataSource

    @Singleton
    @Binds
    abstract fun bindEmergencyInfoDataSource(dataSource: EmergencyInfoFirebaseDataSource): EmergencyInfoDataSource

    @Singleton
    @Binds
    abstract fun bindMeasurementDataSource(dataSource: MeasurementFirebaseDataSource): MeasurementDataSource

    @Singleton
    @Binds
    abstract fun bindMedicalVisitDataSource(dataSource: MedicalVisitFirebaseDataSource): MedicalVisitDataSource

    @Singleton
    @Binds
    abstract fun bindMedicationDataSource(dataSource: MedicationFirebaseDataSource): MedicationDataSource

    @Singleton
    @Binds
    abstract fun bindNotificationDataSource(dataSource: NotificationFirebaseDataSource): NotificationDataSource
}