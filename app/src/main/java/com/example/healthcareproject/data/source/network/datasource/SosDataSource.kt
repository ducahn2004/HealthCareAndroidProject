package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseSos

interface SosDataSource {

    suspend fun writeSos(sos: FirebaseSos)

    suspend fun readSos(sosId: String): FirebaseSos?

    suspend fun deleteSos(sosId: String)

    suspend fun updateSos(sosId: String, sos: FirebaseSos)

    suspend fun readAllSosByUserId(userId: String): List<FirebaseSos>?
}