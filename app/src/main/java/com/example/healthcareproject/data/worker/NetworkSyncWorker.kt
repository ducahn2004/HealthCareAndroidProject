package com.example.healthcareproject.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.healthcareproject.di.RepositoryEntryPoint
import dagger.hilt.android.EntryPointAccessors
import timber.log.Timber

class NetworkSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(
        context.applicationContext,
        RepositoryEntryPoint::class.java
    )
    private val userRepository = entryPoint.userRepository()
    private val emergencyInfoRepository = entryPoint.emergencyInfoRepository()
    private val reminderRepository = entryPoint.reminderRepository()
    private val notificationRepository = entryPoint.notificationRepository()
    private val medicalVisitRepository = entryPoint.medicalVisitRepository()
    private val appointmentRepository = entryPoint.appointmentRepository()
    private val medicationRepository = entryPoint.medicationRepository()
    private val alertRepository = entryPoint.alertRepository()


    override suspend fun doWork(): Result {
        val userId = userRepository.getCurrentUserId()
        if (userId == null) {
            Timber.tag("NetworkSyncWorker").e("No CurrentId found. Skipping sync.")
            return Result.failure()
        }

        Timber.tag("NetworkSyncWorker").d("Sync Data Firebase and Room for userId: $userId")
        return try {
            userRepository.refresh()
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
