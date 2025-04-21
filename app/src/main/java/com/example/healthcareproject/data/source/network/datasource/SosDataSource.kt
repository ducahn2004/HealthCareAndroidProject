package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseSos

interface SosDataSource {

    suspend fun loadSos(userId: String): List<FirebaseSos>

    suspend fun saveSos(sosList: List<FirebaseSos>)
}