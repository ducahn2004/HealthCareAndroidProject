package com.example.healthcareproject.di

import android.content.Context
import com.example.healthcareproject.domain.repository.*
import com.example.healthcareproject.domain.repository.MedicationRepository
import com.example.healthcareproject.domain.usecase.alert.*
import com.example.healthcareproject.domain.usecase.appointment.*
import com.example.healthcareproject.domain.usecase.user.LogoutUseCase
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

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideAlertUseCases(
        alertRepository: AlertRepository,
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
    fun provideUserUseCases(
        userRepository: UserRepository
    ): UserUseCases {
        return UserUseCases(
            createUser = CreateUserUseCase(userRepository),
            deleteUser = DeleteUserUseCase(userRepository),
            updateUser = UpdateUserUseCase(userRepository),
            getUser = GetUserUseCase(userRepository),
            logout = LogoutUseCase(userRepository)
        )
    }

    @Provides
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
    fun provideMedicalVisitUseCases(
        medicalVisitRepository: MedicalVisitRepository,
        medicationRepository: MedicationRepository
    ): MedicalVisitUseCases {
        return MedicalVisitUseCases(
            getMedicalVisitsUseCase = GetMedicalVisitsUseCase(medicalVisitRepository),
            getMedicalVisitUseCase = GetMedicalVisitUseCase(medicalVisitRepository),
            createMedicalVisitUseCase = CreateMedicalVisitUseCase(medicalVisitRepository),
            updateMedicalVisitUseCase = UpdateMedicalVisitUseCase(medicalVisitRepository),
            deleteMedicalVisitUseCase = DeleteMedicalVisitUseCase(medicalVisitRepository),
            addMedicalVisitWithMedicationsUseCase = AddMedicalVisitWithMedicationsUseCase(
                medicalVisitRepository,
                medicationRepository
            )
        )
    }

    @Provides
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
            medicationLogic =  MedicationLogicUseCase(medicationRepository, alertRepository),
            medicationReminderLogic = MedicationReminderLogicUseCase(medicationRepository, alertRepository)
        )
    }

    @Provides
    fun provideMeasurementUseCases(
        measurementRepository: MeasurementRepository,
        getUserUseCase: GetUserUseCase,
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
                sendSosUseCase
            ),
            spO2AnalysisUseCase = SpO2AnalysisUseCase(
                measurementRepository,
                sendSosUseCase
            )
        )
    }

    @Provides
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
    fun provideSosUseCases(
        sosRepository: SosRepository,
        context: Context,
        emergencyInfoRepository: EmergencyInfoRepository,
        createSosUseCase: CreateSosUseCase,
        sosEmergencyCallUseCase: SosEmergencyCallUseCase
    ): SosUseCases {
        return SosUseCases(
            createSos = CreateSosUseCase(sosRepository),
            getSosEvents = GetSosEventsUseCase(sosRepository),
            updateSos = UpdateSosUseCase(sosRepository),
            deleteSos = DeleteSosUseCase(sosRepository),
            emergencyCall = SosEmergencyCallUseCase(context),
            sendSos = SendSosUseCase(
                emergencyInfoRepository,
                createSosUseCase,
                sosEmergencyCallUseCase
            )
        )
    }

    @Provides
    fun provideAppointmentUseCases(
        appointmentRepository: AppointmentRepository,
        userRepository: UserRepository,
        getAppointmentsUseCase: GetAppointmentsUseCase,
        createAlertUseCase: CreateAlertUseCase
    ): AppointmentUseCases {
        return AppointmentUseCases(
            reminderLogic = AppointmentReminderLogicUseCase(getAppointmentsUseCase, createAlertUseCase),
            createAppointment = CreateAppointmentUseCase(appointmentRepository, userRepository),
            getAppointments = GetAppointmentsUseCase(appointmentRepository),
            updateAppointment = UpdateAppointmentUseCase(appointmentRepository),
            deleteAppointment = DeleteAppointmentUseCase(appointmentRepository),
        )
    }
}