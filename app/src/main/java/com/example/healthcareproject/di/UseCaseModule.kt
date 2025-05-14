package com.example.healthcareproject.di

import android.content.Context
import com.example.healthcareproject.domain.repository.*
import com.example.healthcareproject.domain.repository.MedicationRepository
import com.example.healthcareproject.domain.usecase.reminder.*
import com.example.healthcareproject.domain.usecase.appointment.*
import com.example.healthcareproject.domain.usecase.user.LogoutUseCase
import com.example.healthcareproject.domain.usecase.emergencyinfo.*
import com.example.healthcareproject.domain.usecase.medicalvisit.*
import com.example.healthcareproject.domain.usecase.medication.*
import com.example.healthcareproject.domain.usecase.measurement.*
import com.example.healthcareproject.domain.usecase.notification.*
import com.example.healthcareproject.domain.usecase.alert.*
import com.example.healthcareproject.domain.usecase.user.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideReminderUseCases(
        reminderRepository: ReminderRepository,
        context: Context
    ): ReminderUseCases {
        return ReminderUseCases(
            createReminder = CreateReminderUseCase(reminderRepository, context),
            deleteReminder = DeleteReminderUseCase(reminderRepository, context),
            updateReminder = UpdateReminderUseCase(reminderRepository, context),
            getReminders = GetRemindersUseCase(reminderRepository),
            getReminderById = GetReminderByIdUseCase(reminderRepository)
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
        medicationUseCases: MedicationUseCases
    ): MedicalVisitUseCases {
        return MedicalVisitUseCases(
            getMedicalVisitsUseCase = GetMedicalVisitsUseCase(medicalVisitRepository),
            getMedicalVisitUseCase = GetMedicalVisitUseCase(medicalVisitRepository),
            createMedicalVisitUseCase = CreateMedicalVisitUseCase(medicalVisitRepository),
            updateMedicalVisitUseCase = UpdateMedicalVisitUseCase(medicalVisitRepository),
            deleteMedicalVisitUseCase = DeleteMedicalVisitUseCase(medicalVisitRepository),
            addMedicalVisitWithMedicationsUseCase = AddMedicalVisitWithMedicationsUseCase(
                medicalVisitRepository,
                medicationUseCases
            )
        )
    }

    @Provides
    fun provideMedicationUseCases(
        medicationRepository: MedicationRepository,
    ): MedicationUseCases {
        return MedicationUseCases(
            createMedication = CreateMedicationUseCase(medicationRepository),
            getMedications = GetMedicationsUseCase(medicationRepository),
            updateMedication = UpdateMedicationUseCase(medicationRepository),
            deleteMedication = DeleteMedicationUseCase(medicationRepository),
            getMedicationById = GetMedicationByIdUseCase(medicationRepository),
            getMedicationsByVisitId = GetMedicationsByVisitIdUseCase(medicationRepository)
        )
    }

    @Provides
    fun provideMeasurementUseCases(
        measurementRepository: MeasurementRepository,
        getUserUseCase: GetUserUseCase,
        getMedicalVisitUseCase: GetMedicalVisitsUseCase,
        sendAlertUseCase: SendAlertUseCase

    ): MeasurementUseCases {
        return MeasurementUseCases(
            getMeasurementsUseCase = GetMeasurementsUseCase(measurementRepository),
            getMeasurementRealTimeUseCase = GetMeasurementRealTimeUseCase(measurementRepository),
            deleteMeasurementUseCase = DeleteMeasurementUseCase(measurementRepository),
            hRAnalysisUseCase = HRAnalysisUseCase(
                measurementRepository,
                getUserUseCase,
                getMedicalVisitUseCase,
                sendAlertUseCase
            ),
            spO2AnalysisUseCase = SpO2AnalysisUseCase(
                measurementRepository,
                sendAlertUseCase
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
            updateNotificationUseCase = UpdateNotificationUseCase(notificationRepository),
            deleteNotificationUseCase = DeleteNotificationUseCase(notificationRepository)
        )
    }

    @Provides
    fun provideAlertUseCases(
        alertRepository: AlertRepository,
        context: Context,
        emergencyInfoRepository: EmergencyInfoRepository,
        createAlertUseCase: CreateAlertUseCase,
        alertCallUseCase: AlertCallUseCase
    ): AlertUseCases {
        return AlertUseCases(
            createAlert = CreateAlertUseCase(alertRepository),
            deleteAlert = DeleteAlertUseCase(alertRepository),
            emergencyCall = AlertCallUseCase(context),
            sendAlert = SendAlertUseCase(
                emergencyInfoRepository,
                createAlertUseCase,
                alertCallUseCase
            )
        )
    }

    @Provides
    fun provideAppointmentUseCases(
        appointmentRepository: AppointmentRepository,
        userRepository: UserRepository,
    ): AppointmentUseCases {
        return AppointmentUseCases(
            createAppointment = CreateAppointmentUseCase(appointmentRepository, userRepository),
            getAppointments = GetAppointmentsUseCase(appointmentRepository),
            updateAppointment = UpdateAppointmentUseCase(appointmentRepository),
            deleteAppointment = DeleteAppointmentUseCase(appointmentRepository),
        )
    }
}