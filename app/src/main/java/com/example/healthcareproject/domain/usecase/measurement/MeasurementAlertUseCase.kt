package com.example.healthcareproject.domain.usecase.measurement

import android.content.Context
import android.content.Intent
import com.example.healthcareproject.domain.model.Measurement
import com.example.healthcareproject.domain.repository.AlertRepository
import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import com.example.healthcareproject.present.receiver.CallAlertReceiver
import com.example.healthcareproject.present.util.NotificationUtil
import javax.inject.Inject

class MeasurementAlertUseCase @Inject constructor(
    private val emergencyRepo: EmergencyInfoRepository,
    private val alertRepository: AlertRepository,
    private val hrAnalysisUseCase: HRAnalysisUseCase,
    private val spO2AnalysisUseCase: SpO2AnalysisUseCase
) {
    suspend fun checkThresholdAndAlert(context: Context, measurement: Measurement) {
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
                action = "com.example.healthcareproject.ALERT_CALL"
                putExtra("phoneNumber", topPriorityEmergency.emergencyPhone)
            }
            context.sendBroadcast(intent)
        }
    }
}