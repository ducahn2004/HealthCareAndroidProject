package com.example.healthcareproject.data.woker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.healthcareproject.domain.repository.AlertRepository
import com.example.healthcareproject.domain.repository.AppointmentRepository
import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import com.example.healthcareproject.domain.repository.NotificationRepository
import com.example.healthcareproject.domain.repository.ReminderRepository
import com.example.healthcareproject.domain.repository.UserRepository
import javax.inject.Inject

class NetworkSyncWorker @Inject constructor(
    context: Context,
    workerParams: WorkerParameters,
    private val alertRepository: AlertRepository,
    private val appointmentRepository: AppointmentRepository,
    private val emergencyInfoRepository: EmergencyInfoRepository,
    private val notificationRepository: NotificationRepository,
    private val reminderRepository: ReminderRepository,
    private val userRepository: UserRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            alertRepository.refresh()
            appointmentRepository.refresh()
            emergencyInfoRepository.refresh()
            notificationRepository.refresh()
            reminderRepository.refresh()
            userRepository.refresh()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}