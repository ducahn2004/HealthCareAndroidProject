package com.example.healthcareproject.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import timber.log.Timber

object WorkerScheduler {
    fun scheduleNetworkSyncWorker(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWork = OneTimeWorkRequestBuilder<NetworkSyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "NetworkSyncWorker",
                ExistingWorkPolicy.KEEP,
                syncWork
            )

        Timber.tag("WorkerScheduler").d("NetworkSyncWorker scheduled with constraints: $constraints")
    }
}