package com.example.healthcareproject.data.repository

import androidx.room.withTransaction
import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.AppDatabase
import com.example.healthcareproject.data.source.local.dao.MedicalVisitDao
import com.example.healthcareproject.data.source.network.datasource.AuthDataSource
import com.example.healthcareproject.data.source.network.datasource.MedicalVisitDataSource
import com.example.healthcareproject.di.DefaultDispatcher
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import com.example.healthcareproject.domain.repository.MedicationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMedicalVisitRepository @Inject constructor(
    private val networkDataSource: MedicalVisitDataSource,
    private val localDataSource: MedicalVisitDao,
    private val authDataSource: AuthDataSource,
    private val medicationRepository: MedicationRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    private val appDatabase: AppDatabase
) : MedicalVisitRepository {

    private val userId: String
        get() = authDataSource.getCurrentUserId() ?: throw Exception("User not logged in")

    override suspend fun createMedicalVisit(
        visitId: String,
        visitReason: String,
        visitDate: LocalDate,
        doctorName: String,
        notes: String?,
        status: Boolean
    ): String {
        return appDatabase.withTransaction {
            try {
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

                Timber.d("Creating medical visit locally: $medicalVisit")
                localDataSource.upsert(medicalVisit.toLocal())
                saveMedicalVisitsToNetwork()

                Timber.d("Medical visit created with ID: $visitId")
                visitId
            } catch (e: Exception) {
                Timber.e(e, "Error creating medical visit")
                throw e
            }
        }
    }

    override suspend fun updateMedicalVisit(
        medicalVisitId: String,
        visitReason: String,
        visitDate: LocalDate,
        doctorName: String,
        notes: String?,
        status: Boolean
    ) {
        appDatabase.withTransaction {
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
            appDatabase.withTransaction {
                val remoteVisits = networkDataSource.loadMedicalVisits(userId)
                localDataSource.upsertAll(remoteVisits.toLocal())
                saveMedicalVisitsToNetwork()
            }
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
        appDatabase.withTransaction {
            val medicalVisit = getMedicalVisit(medicalVisitId)?.copy(treatment = "Active")
                ?: throw Exception("MedicalVisit (id $medicalVisitId) not found")
            localDataSource.upsert(medicalVisit.toLocal())
            saveMedicalVisitsToNetwork()
        }
    }

    override suspend fun deactivateMedicalVisit(medicalVisitId: String) {
        appDatabase.withTransaction {
            val medicalVisit = getMedicalVisit(medicalVisitId)?.copy(treatment = "Inactive")
                ?: throw Exception("MedicalVisit (id $medicalVisitId) not found")
            localDataSource.upsert(medicalVisit.toLocal())
            saveMedicalVisitsToNetwork()
        }
    }

    override suspend fun clearInactiveMedicalVisits() {
        appDatabase.withTransaction {
            localDataSource.deleteByUserId(userId)
            saveMedicalVisitsToNetwork()
        }
    }

    override suspend fun deleteAllMedicalVisits() {
        appDatabase.withTransaction {
            localDataSource.deleteAll()
            saveMedicalVisitsToNetwork()
        }
    }

    override suspend fun deleteMedicalVisit(medicalVisitId: String) {
        appDatabase.withTransaction {
            localDataSource.deleteById(medicalVisitId)
            saveMedicalVisitsToNetwork()
        }
    }

    override suspend fun saveMedicalVisitsToNetwork() {
        try {
            val localVisits = localDataSource.getAll()
            if (localVisits.isNotEmpty()) {
                val networkVisits = localVisits.toExternal().map { it.toNetwork() }
                Timber.d("Syncing ${networkVisits.size} medical visits to network")
                networkDataSource.saveMedicalVisits(networkVisits)
            } else {
                Timber.d("No medical visits to sync")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync medical visits")
            throw e
        }
    }

    override suspend fun withTransaction(block: suspend () -> Unit) {
        appDatabase.withTransaction {
            try {
                Timber.d("Starting database transaction")
                block()
                Timber.d("Local transaction completed, syncing to network")
                saveMedicalVisitsToNetwork()
                medicationRepository.saveMedicationsToNetwork()
                Timber.d("Network sync completed successfully")
            } catch (e: Exception) {
                Timber.e(e, "Transaction failed: ${e.message}")
                throw e
            }
        }
    }
}