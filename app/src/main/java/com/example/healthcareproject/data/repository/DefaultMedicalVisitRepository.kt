package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.dao.MedicalVisitDao
import com.example.healthcareproject.data.source.network.datasource.AuthDataSource
import com.example.healthcareproject.data.source.network.datasource.MedicalVisitDataSource
import com.example.healthcareproject.di.ApplicationScope
import com.example.healthcareproject.di.DefaultDispatcher
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMedicalVisitRepository @Inject constructor(
    private val networkDataSource: MedicalVisitDataSource,
    private val localDataSource: MedicalVisitDao,
    private val authDataSource: AuthDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : MedicalVisitRepository {

    private val userId: String
        get() = authDataSource.getCurrentUserId() ?: throw Exception("User not logged in")

    override suspend fun createMedicalVisit(
        patientName: String,
        visitReason: String,
        visitDate: LocalDate,
        doctorName: String,
        notes: String?,
        status: Boolean
    ): String {
        val visitId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val medicalVisit = MedicalVisit(
            visitId = visitId,
            userId = userId,
            visitDate = visitDate,
            clinicName = visitReason,
            doctorName = doctorName,
            diagnosis = notes ?: "",
            treatment = if (status) "Active" else "Inactive",
            createdAt = java.time.LocalDateTime.now()
        )
        localDataSource.upsert(medicalVisit.toLocal())
        saveMedicalVisitsToNetwork()
        return visitId
    }

    override suspend fun updateMedicalVisit(
        medicalVisitId: String,
        patientName: String,
        visitReason: String,
        visitDate: LocalDate,
        doctorName: String,
        notes: String?,
        status: Boolean
    ) {
        val medicalVisit = getMedicalVisit(medicalVisitId)?.copy(
            visitDate = visitDate,
            clinicName = visitReason,
            doctorName = doctorName,
            diagnosis = notes ?: "",
            treatment = if (status) "Active" else "Inactive"
        ) ?: throw Exception("MedicalVisit (id $medicalVisitId) not found")

        localDataSource.upsert(medicalVisit.toLocal())
        saveMedicalVisitsToNetwork()
    }

    override fun getMedicalVisitsStream(): Flow<List<MedicalVisit>> {
        return localDataSource.observeAll()
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override fun getMedicalVisitStream(medicalVisitId: String): Flow<MedicalVisit?> {
        return localDataSource.observeById(medicalVisitId)
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun getMedicalVisits(forceUpdate: Boolean): List<MedicalVisit> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAll().toExternal()
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteVisits = networkDataSource.loadMedicalVisits(userId)
            localDataSource.deleteAll()
            localDataSource.upsertAll(remoteVisits.toLocal())
        }
    }

    override suspend fun getMedicalVisit(medicalVisitId: String, forceUpdate: Boolean): MedicalVisit? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(medicalVisitId)?.toExternal()
    }

    override suspend fun refreshMedicalVisit(medicalVisitId: String) {
        refresh()
    }

    override suspend fun activateMedicalVisit(medicalVisitId: String) {
        val medicalVisit = getMedicalVisit(medicalVisitId)?.copy(treatment = "Active")
            ?: throw Exception("MedicalVisit (id $medicalVisitId) not found")
        localDataSource.upsert(medicalVisit.toLocal())
        saveMedicalVisitsToNetwork()
    }

    override suspend fun deactivateMedicalVisit(medicalVisitId: String) {
        val medicalVisit = getMedicalVisit(medicalVisitId)?.copy(treatment = "Inactive")
            ?: throw Exception("MedicalVisit (id $medicalVisitId) not found")
        localDataSource.upsert(medicalVisit.toLocal())
        saveMedicalVisitsToNetwork()
    }

    override suspend fun clearInactiveMedicalVisits() {
        localDataSource.deleteByUserId(userId)
        saveMedicalVisitsToNetwork()
    }

    override suspend fun deleteAllMedicalVisits() {
        localDataSource.deleteAll()
        saveMedicalVisitsToNetwork()
    }

    override suspend fun deleteMedicalVisit(medicalVisitId: String) {
        localDataSource.deleteById(medicalVisitId)
        saveMedicalVisitsToNetwork()
    }

    private fun saveMedicalVisitsToNetwork() {
        scope.launch {
            try {
                val localVisits = localDataSource.getAll()
                val networkVisits = withContext(dispatcher) {
                    localVisits.toNetwork()
                }
                networkDataSource.saveMedicalVisits(networkVisits)
            } catch (e: Exception) {
                // Log or handle the exception
            }
        }
    }
}