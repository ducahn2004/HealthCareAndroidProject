package com.example.healthcareproject.data.repository

import com.example.healthcareproject.domain.model.Appointment
import com.example.healthcareproject.domain.model.EmergencyInfo
import com.example.healthcareproject.domain.model.Relationship
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the data layer for emergency information.
 */
interface EmergencyInfoRepository {

    suspend fun createEmergencyInfo(
        contactName: String,
        contactNumber: String,
        relationship: Relationship,
        notes: String?,
        status: Boolean
    ): String

    suspend fun updateEmergencyInfo(
        emergencyInfoId: String,
        contactName: String,
        contactNumber: String,
        relationship: Relationship,
        notes: String?,
        status: Boolean
    )

    suspend fun getEmergencyInfos(forceUpdate: Boolean = false): List<EmergencyInfo>

    suspend fun refresh()

    suspend fun getEmergencyInfo(emergencyInfoId: String, forceUpdate: Boolean = false): EmergencyInfo?

    suspend fun refreshEmergencyInfo(emergencyInfoId: String)

    fun getEmergencyInfosStream(): Flow<List<EmergencyInfo>>

    fun getEmergencyInfoStream(emergencyInfoId: String): Flow<EmergencyInfo?>

    suspend fun activateEmergencyInfo(emergencyInfoId: String)

    suspend fun deactivateEmergencyInfo(emergencyInfoId: String)

    suspend fun clearInactiveEmergencyInfos()

    suspend fun deleteAllEmergencyInfos()

    suspend fun deleteEmergencyInfo(emergencyInfoId: String)
}