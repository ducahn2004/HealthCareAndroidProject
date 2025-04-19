package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseAlert

interface AlertDataSource {
    suspend fun writeAlert(alert: FirebaseAlert)

    suspend fun readAlert(alertId: String): FirebaseAlert?

    suspend fun deleteAlert(alertId: String)

    suspend fun updateAlert(alertId: String, alert: FirebaseAlert)

    suspend fun readAllAlertsByUserId(userId: String): List<FirebaseAlert>?
}