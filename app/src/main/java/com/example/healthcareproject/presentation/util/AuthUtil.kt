package com.example.healthcareproject.presentation.util

import android.content.Context
import com.example.healthcareproject.presentation.service.ForegroundServiceStarter
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

object AuthUtil {
    private val auth = FirebaseAuth.getInstance()

    fun init(context: Context) {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Timber.d("User logged in: ${user.uid}")
                ForegroundServiceStarter.startMeasurementService(context)
            } else {
                Timber.d("User logged out")
                ForegroundServiceStarter.stopMeasurementService(context)
            }
        }
    }
}
