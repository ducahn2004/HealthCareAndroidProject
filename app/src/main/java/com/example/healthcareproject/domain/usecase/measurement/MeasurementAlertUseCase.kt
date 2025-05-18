package com.example.healthcareproject.domain.usecase.measurement

import android.content.Context
import android.content.Intent
import com.example.healthcareproject.domain.model.Measurement
import com.example.healthcareproject.domain.repository.AlertRepository
import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import com.example.healthcareproject.domain.usecase.alert.AlertThrottleManager
import com.example.healthcareproject.presentation.receiver.CallAlertReceiver
import com.example.healthcareproject.presentation.util.NotificationUtil
import timber.log.Timber
import javax.inject.Inject

class MeasurementAlertUseCase @Inject constructor(
    private val emergencyRepo: EmergencyInfoRepository,
    private val alertRepository: AlertRepository,
    private val hrAnalysisUseCase: HRAnalysisUseCase,
    private val spO2AnalysisUseCase: SpO2AnalysisUseCase
) {
    suspend fun checkThresholdAndAlert(context: Context, measurement: Measurement) {
        Timber.tag("MeasurementAlertUseCase")
            .d("Checking thresholds for measurement: ${measurement.measurementId}")

        if (!AlertThrottleManager.shouldTriggerAlert()) {
            Timber.tag("MeasurementAlertUseCase")
                .d("Alert throttled - skipping alert for measurement: ${measurement.measurementId}")
            return
        }

        if (hrAnalysisUseCase.isAbnormal(measurement.bpm) || spO2AnalysisUseCase.isAbnormal(measurement.spO2)) {
            val emergencyInfos = emergencyRepo.getEmergencyInfos()
                .sortedBy { it.priority }

            val topPriorityEmergency = emergencyInfos.firstOrNull() ?: return

            alertRepository.createAlert(
                measurementId = measurement.measurementId,
                emergencyId = topPriorityEmergency.emergencyId,
                triggerReason = "Abnormal health indicators",
                contacted = false
            )

            NotificationUtil.showWarningNotification(
                context,
                "Measurement Alert",
                "Abnormal health indicators! Call ${topPriorityEmergency.emergencyName}"
            )

            val intent = Intent(context, CallAlertReceiver::class.java).apply {
                action = "ACTION_CALL_ALERT"
                putExtra("phoneNumber", topPriorityEmergency.emergencyPhone)
                putExtra("title", "Health Alert")
                putExtra("message", "Abnormal health indicators! Calling ${topPriorityEmergency.emergencyName}")
            }

            context.sendBroadcast(intent)
        }
    }
}