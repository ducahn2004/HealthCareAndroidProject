package com.example.healthcareproject.present.service

import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.healthcareproject.R
import com.example.healthcareproject.domain.repository.MeasurementRepository
import com.example.healthcareproject.domain.usecase.measurement.MeasurementAlertUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MeasurementMonitorService : LifecycleService() {

    @Inject
    lateinit var measurementRepository: MeasurementRepository
    @Inject
    lateinit var alertUseCase: MeasurementAlertUseCase

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        observeMeasurements()
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, "alert_channel")
            .setContentTitle("Monitoring Measurements")
            .setSmallIcon(R.drawable.ic_heart_rate)
            .build()
        startForeground(1, notification)
    }

    private fun observeMeasurements() {
        lifecycleScope.launch {
            measurementRepository.getMeasurementsRealtime().collect { measurements ->
                measurements.forEach { measurement ->
                    alertUseCase.checkThresholdAndAlert(this@MeasurementMonitorService, measurement)
                }
            }
        }
    }
}
