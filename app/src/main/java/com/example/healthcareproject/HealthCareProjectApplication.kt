package com.example.healthcareproject

import android.app.Application
import com.example.healthcareproject.present.util.NotificationUtil
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class HealthCareProjectApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        NotificationUtil.createAlertNotificationChannel(this)

        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
        FirebaseApp.initializeApp(this)
        Timber.plant(DebugTree())
    }
}