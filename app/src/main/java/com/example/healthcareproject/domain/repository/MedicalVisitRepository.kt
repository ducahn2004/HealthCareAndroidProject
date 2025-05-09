package com.example.healthcareproject.domain.repository

import com.example.healthcareproject.domain.model.MedicalVisit
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interface to the data layer for medical visits.
 */
interface MedicalVisitRepository {

    suspend fun createMedicalVisit(
        visitId: String,
        patientName: String,
        visitReason: String,
        visitDate: LocalDate,
        doctorName: String,
        notes: String?,
        status: Boolean
    ): String

    suspend fun updateMedicalVisit(
        medicalVisitId: String,
        patientName: String,
        visitReason: String,
        visitDate: LocalDate,
        doctorName: String,
        notes: String?,
        status: Boolean
    )

    fun getMedicalVisitsStream(): Flow<List<MedicalVisit>>

    fun getMedicalVisitStream(medicalVisitId: String): Flow<MedicalVisit?>

    suspend fun getMedicalVisits(forceUpdate: Boolean = false): List<MedicalVisit>

    suspend fun refresh()

    suspend fun getMedicalVisit(medicalVisitId: String, forceUpdate: Boolean = false): MedicalVisit?

    suspend fun refreshMedicalVisit(medicalVisitId: String)

    suspend fun activateMedicalVisit(medicalVisitId: String)

    suspend fun deactivateMedicalVisit(medicalVisitId: String)

    suspend fun clearInactiveMedicalVisits()

    suspend fun deleteAllMedicalVisits()

    suspend fun deleteMedicalVisit(medicalVisitId: String)

    suspend fun withTransaction(block: suspend () -> Unit)
}