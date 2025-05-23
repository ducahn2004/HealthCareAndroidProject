package com.example.healthcareproject

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.healthcareproject.data.worker.WorkerScheduler
import com.example.healthcareproject.presentation.util.AuthUtil
import com.example.healthcareproject.presentation.util.NotificationUtil
import com.example.healthcareproject.presentation.util.SessionManagerUtil
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class HealthCareProjectApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        AuthUtil.init(this)

        NotificationUtil.createAlertNotificationChannel(this)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        val userId = SessionManagerUtil.currentUserId
        if (userId != null) {
            WorkerScheduler.scheduleNetworkSyncWorker(this)
        } else {
            Timber.tag("AppStart").d("No user logged in -> Worker not scheduled")
        }
    }
}
