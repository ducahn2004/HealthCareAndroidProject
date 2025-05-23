package com.example.healthcareproject.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.healthcareproject.domain.repository.AlertRepository
import com.example.healthcareproject.domain.repository.AppointmentRepository
import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import com.example.healthcareproject.domain.repository.MedicationRepository
import com.example.healthcareproject.domain.repository.NotificationRepository
import com.example.healthcareproject.domain.repository.ReminderRepository
import com.example.healthcareproject.domain.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class NetworkSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val alertRepository: AlertRepository,
    private val appointmentRepository: AppointmentRepository,
    private val emergencyInfoRepository: EmergencyInfoRepository,
    private val medicalVisitRepository: MedicalVisitRepository,
    private val notificationRepository: NotificationRepository,
    private val reminderRepository: ReminderRepository,
    private val medicationRepository: MedicationRepository,
    private val userRepository: UserRepository
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val userId = userRepository.getCurrentUserId()
        if (userId == null) {
            Timber.tag("NetworkSyncWorker").e("No CurrentId found. Skipping sync.")
            return Result.failure()
        }

        Timber.tag("NetworkSyncWorker").d("Sync Data Firebase and Room for userId: $userId")
        return try {
            emergencyInfoRepository.refresh()
            alertRepository.refresh()
            medicalVisitRepository.refresh()
            medicationRepository.refresh()
            appointmentRepository.refresh()
            reminderRepository.refresh()
            notificationRepository.refresh()

            Result.success()
        } catch (e: Exception) {
            Timber.tag("NetworkSyncWorker").e(e, "Error during sync")
            Result.failure()
        }
    }
}
