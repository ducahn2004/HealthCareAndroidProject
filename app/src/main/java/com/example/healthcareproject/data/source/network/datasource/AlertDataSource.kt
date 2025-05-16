package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseAlert

interface AlertDataSource {

    suspend fun loadAlerts(userId: String): List<FirebaseAlert>

    suspend fun saveAlerts(alertList: List<FirebaseAlert>)
}