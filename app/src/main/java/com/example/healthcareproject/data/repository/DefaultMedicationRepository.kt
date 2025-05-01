package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.dao.MedicationDao
import com.example.healthcareproject.data.source.network.datasource.AuthDataSource
import com.example.healthcareproject.data.source.network.datasource.MedicationDataSource
import com.example.healthcareproject.di.ApplicationScope
import com.example.healthcareproject.di.DefaultDispatcher
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.repository.MedicationRepository
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
class DefaultMedicationRepository @Inject constructor(
    private val networkDataSource: MedicationDataSource,
    private val localDataSource: MedicationDao,
    private val authDataSource: AuthDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : MedicationRepository {

    private val userId: String
        get() = authDataSource.getCurrentUserId() ?: throw Exception("User not logged in")

    override suspend fun createMedication(
        name: String,
        dosageUnit: DosageUnit,
        dosageAmount: Float,
        frequency: Int,
        timeOfDay: List<String>,
        mealRelation: MealRelation,
        startDate: LocalDate,
        endDate: LocalDate,
        notes: String
    ): String {
        val medicationId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val medication = Medication(
            medicationId = medicationId,
            userId = userId,
            visitId = null, // Replace with actual visitId logic if needed
            name = name,
            dosageUnit = dosageUnit,
            dosageAmount = dosageAmount,
            frequency = frequency,
            timeOfDay = timeOfDay,
            mealRelation = mealRelation,
            startDate = startDate,
            endDate = endDate,
            notes = notes
        )
        localDataSource.upsert(medication.toLocal())
        saveMedicationsToNetwork()
        return medicationId
    }

    override suspend fun updateMedication(
        medicationId: String,
        name: String,
        dosageUnit: DosageUnit,
        dosageAmount: Float,
        frequency: Int,
        timeOfDay: List<String>,
        mealRelation: MealRelation,
        startDate: LocalDate,
        endDate: LocalDate,
        notes: String
    ) {
        val medication = getMedication(medicationId)?.copy(
            name = name,
            dosageUnit = dosageUnit,
            dosageAmount = dosageAmount,
            frequency = frequency,
            timeOfDay = timeOfDay,
            mealRelation = mealRelation,
            startDate = startDate,
            endDate = endDate,
            notes = notes
        ) ?: throw Exception("Medication (id $medicationId) not found")

        localDataSource.upsert(medication.toLocal())
        saveMedicationsToNetwork()
    }

    override fun getMedicationsStream(): Flow<List<Medication>> {
        return localDataSource.observeAll()
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override fun getMedicationStream(medicationId: String): Flow<Medication?> {
        return localDataSource.observeById(medicationId)
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun getMedications(forceUpdate: Boolean): List<Medication> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAll().toExternal()
        }
    }

    override suspend fun getMedicationsByVisitId(visitId: String, forceUpdate: Boolean): List<Medication> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAll().toExternal().filter { it.visitId == visitId }
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteMedications = networkDataSource.loadMedications(userId)
            localDataSource.deleteAll()
            localDataSource.upsertAll(remoteMedications.toLocal())
        }
    }

    override suspend fun getMedication(medicationId: String, forceUpdate: Boolean): Medication? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(medicationId)?.toExternal()
    }

    override suspend fun refreshMedication(medicationId: String) {
        refresh()
    }

    override suspend fun activateMedication(medicationId: String) {
        val medication = getMedication(medicationId)?.copy(notes = "Activated")
            ?: throw Exception("Medication (id $medicationId) not found")
        localDataSource.upsert(medication.toLocal())
        saveMedicationsToNetwork()
    }

    override suspend fun deactivateMedication(medicationId: String) {
        val medication = getMedication(medicationId)?.copy(notes = "Deactivated")
            ?: throw Exception("Medication (id $medicationId) not found")
        localDataSource.upsert(medication.toLocal())
        saveMedicationsToNetwork()
    }

    override suspend fun clearInactiveMedications() {
        localDataSource.deleteByUserId(userId)
        saveMedicationsToNetwork()
    }

    override suspend fun deleteAllMedications() {
        localDataSource.deleteAll()
        saveMedicationsToNetwork()
    }

    override suspend fun deleteMedication(medicationId: String) {
        localDataSource.deleteById(medicationId)
        saveMedicationsToNetwork()
    }

    private fun saveMedicationsToNetwork() {
        scope.launch {
            try {
                val localMedications = localDataSource.getAll()
                val networkMedications = withContext(dispatcher) {
                    localMedications.toNetwork()
                }
                networkDataSource.saveMedications(networkMedications)
            } catch (e: Exception) {
                // Log or handle the exception
            }
        }
    }
}