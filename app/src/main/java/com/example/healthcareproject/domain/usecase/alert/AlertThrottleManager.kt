package com.example.healthcareproject.domain.usecase.alert

object AlertThrottleManager {
    private const val THROTTLE_DURATION_MS = 3 * 60 * 1000L
    private var lastAlertTimestamp: Long = 0

    fun shouldTriggerAlert(): Boolean {
        val currentTime = System.currentTimeMillis()
        return if (currentTime - lastAlertTimestamp >= THROTTLE_DURATION_MS) {
            lastAlertTimestamp = currentTime
            true
        } else {
            false
        }
    }
}
