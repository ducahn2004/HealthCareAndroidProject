package com.example.healthcareproject.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.healthcareproject.domain.repository.AlertRepository
import com.example.healthcareproject.domain.repository.AppointmentRepository
import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import com.example.healthcareproject.domain.repository.NotificationRepository
import com.example.healthcareproject.domain.repository.ReminderRepository
import timber.log.Timber
import javax.inject.Inject

class NetworkSyncWorker @Inject constructor(
    context: Context,
    workerParams: WorkerParameters,
    private val alertRepository: AlertRepository,
    private val appointmentRepository: AppointmentRepository,
    private val emergencyInfoRepository: EmergencyInfoRepository,
    private val notificationRepository: NotificationRepository,
    private val reminderRepository: ReminderRepository,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Timber.tag("NetworkSyncWorker").d("Sync Data Firebase and Room")
        return try {
            alertRepository.refresh()
            appointmentRepository.refresh()
            emergencyInfoRepository.refresh()
            notificationRepository.refresh()
            reminderRepository.refresh()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}