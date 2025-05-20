package com.example.healthcareproject

import android.app.Application
import androidx.work.Configuration
import com.example.healthcareproject.data.woker.WorkerScheduler
import com.example.healthcareproject.presentation.util.NotificationUtil
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class HealthCareProjectApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        NotificationUtil.createAlertNotificationChannel(this)

        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
        FirebaseApp.initializeApp(this)

        WorkerScheduler.scheduleNetworkSyncWorker(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}