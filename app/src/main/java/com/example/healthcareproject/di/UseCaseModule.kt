package com.example.healthcareproject.di

import com.example.healthcareproject.domain.repository.*
import com.example.healthcareproject.domain.usecase.alert.*
import com.example.healthcareproject.domain.usecase.emergencyinfo.*
import com.example.healthcareproject.domain.usecase.medicalvisit.*
import com.example.healthcareproject.domain.usecase.medication.*
import com.example.healthcareproject.domain.usecase.measurement.*
import com.example.healthcareproject.domain.usecase.notification.*
import com.example.healthcareproject.domain.usecase.sos.*
import com.example.healthcareproject.domain.usecase.user.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideAlertUseCases(
        alertRepository: AlertRepository
    ): AlertUseCases {
        return AlertUseCases(
            createAlert = CreateAlertUseCase(alertRepository),
            deleteAlert = DeleteAlertUseCase(alertRepository),
            updateAlert = UpdateAlertUseCase(alertRepository),
            getAlerts = GetAlertsUseCase(alertRepository),
            getAlertById = GetAlertByIdUseCase(alertRepository),
            alertLogic = AlertLogicUseCase(alertRepository)
        )
    }

    @Provides
    @Singleton
    fun provideUserUseCases(
        userRepository: UserRepository
    ): UserUseCases {
        return UserUseCases(
            createUser = CreateUserUseCase(userRepository),
            deleteUser = DeleteUserUseCase(userRepository),
            updateUser = UpdateUserUseCase(userRepository),
            getUser = GetUserUseCase(userRepository),
            login = LoginUserUseCase(userRepository),
            logout = LogoutUseCase(userRepository)
        )
    }

    @Provides
    @Singleton
    fun provideEmergencyInfoUseCases(
        emergencyInfoRepository: EmergencyInfoRepository
    ): EmergencyInfoUseCases {
        return EmergencyInfoUseCases(
            createEmergencyInfo = CreateEmergencyInfoUseCase(emergencyInfoRepository),
            getEmergencyInfos = GetEmergencyInfosUseCase(emergencyInfoRepository),
            updateEmergencyInfo = UpdateEmergencyInfoUseCase(emergencyInfoRepository),
            deleteEmergencyInfo = DeleteEmergencyInfoUseCase(emergencyInfoRepository),
            getEmergencyInfoById = GetEmergencyInfoByIdUseCase(emergencyInfoRepository),
            emergencyInfoLogic = EmergencyInfoLogicUseCase(emergencyInfoRepository)
        )
    }

    @Provides
    @Singleton
    fun provideMedicalVisitUseCases(
        medicalVisitRepository: MedicalVisitRepository,
        createMedicalVisitUseCase: CreateMedicalVisitUseCase,
        createMedicationUseCase: CreateMedicationUseCase
    ): MedicalVisitUseCases {
        return MedicalVisitUseCases(
            getMedicalVisitsUseCase = GetMedicalVisitsUseCase(medicalVisitRepository),
            getMedicalVisitUseCase = GetMedicalVisitUseCase(medicalVisitRepository),
            createMedicalVisitUseCase = createMedicalVisitUseCase,
            updateMedicalVisitUseCase = UpdateMedicalVisitUseCase(medicalVisitRepository),
            deleteMedicalVisitUseCase = DeleteMedicalVisitUseCase(medicalVisitRepository),
            addMedicalVisitWithMedicationsUseCase = AddMedicalVisitWithMedicationsUseCase(
                createMedicalVisitUseCase,
                createMedicationUseCase
            )
        )
    }

    @Provides
    @Singleton
    fun provideMedicationUseCases(
        medicationRepository: MedicationRepository,
        alertRepository: AlertRepository
    ): MedicationUseCases {
        return MedicationUseCases(
            createMedication = CreateMedicationUseCase(medicationRepository),
            getMedications = GetMedicationsUseCase(medicationRepository),
            updateMedication = UpdateMedicationUseCase(medicationRepository),
            deleteMedication = DeleteMedicationUseCase(medicationRepository),
            getMedicationById = GetMedicationByIdUseCase(medicationRepository),
            getMedicationsByVisitId = GetMedicationsByVisitIdUseCase(medicationRepository),
            medicationLogic = MedicationLogicUseCase(medicationRepository, alertRepository),
            medicationReminderLogic = MedicationReminderLogicUseCase(medicationRepository, alertRepository)
        )
    }

    @Provides
    @Singleton
    fun provideMeasurementUseCases(
        measurementRepository: MeasurementRepository,
        getUserUseCase: GetUserUseCase,
        getMedicalVisitUseCase: GetMedicalVisitsUseCase,
        sendSosUseCase: SendSosUseCase
    ): MeasurementUseCases {
        return MeasurementUseCases(
            getMeasurementsUseCase = GetMeasurementsUseCase(measurementRepository),
            createMeasurementUseCase = CreateMeasurementUseCase(measurementRepository),
            deleteMeasurementUseCase = DeleteMeasurementUseCase(measurementRepository),
            updateMeasurementUseCase = UpdateMeasurementUseCase(measurementRepository),
            hRAnalysisUseCase = HRAnalysisUseCase(
                measurementRepository,
                getUserUseCase,
                getMedicalVisitUseCase,
                sendSosUseCase
            ),
            spO2AnalysisUseCase = SpO2AnalysisUseCase(
                measurementRepository,
                sendSosUseCase
            )
        )
    }

    @Provides
    @Singleton
    fun provideNotificationUseCases(
        notificationRepository: NotificationRepository
    ): NotificationUseCases {
        return NotificationUseCases(
            getNotificationsUseCase = GetNotificationsUseCase(notificationRepository),
            getNotificationUseCase = GetNotificationUseCase(notificationRepository),
            createNotificationUseCase = CreateNotificationUseCase(notificationRepository),
            updateNotificationUseCase = UpdateNotificationUseCase(notificationRepository),
            deleteNotificationUseCase = DeleteNotificationUseCase(notificationRepository)
        )
    }

    @Provides
    @Singleton
    fun provideSosUseCases(
        sosRepository: SosRepository,
        emergencyInfoRepository: EmergencyInfoRepository,
        createSosUseCase: CreateSosUseCase,
        sosEmergencyCallUseCase: SosEmergencyCallUseCase
    ): SosUseCases {
        return SosUseCases(
            createSos = createSosUseCase,
            getSosEvents = GetSosEventsUseCase(sosRepository),
            updateSos = UpdateSosUseCase(sosRepository),
            deleteSos = DeleteSosUseCase(sosRepository),
            emergencyCall = sosEmergencyCallUseCase,
            sendSos = SendSosUseCase(
                emergencyInfoRepository,
                createSosUseCase,
                sosEmergencyCallUseCase
            )
        )
    }
}