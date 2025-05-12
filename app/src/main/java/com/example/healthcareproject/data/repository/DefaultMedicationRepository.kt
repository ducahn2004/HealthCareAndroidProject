package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.dao.MedicationDao
import com.example.healthcareproject.data.source.network.datasource.AuthDataSource
import com.example.healthcareproject.data.source.network.datasource.MedicationDataSource
import com.example.healthcareproject.data.source.network.model.FirebaseMedication
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
import timber.log.Timber
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
        visitId: String?,
        name: String,
        dosageUnit: DosageUnit,
        dosageAmount: Float,
        frequency: Int,
        timeOfDay: List<String>,
        mealRelation: MealRelation,
        startDate: LocalDate,
        endDate: LocalDate,
        notes: String,
        syncToNetwork: Boolean
    ): String {
        val medicationId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val medication = Medication(
            medicationId = medicationId,
            userId = userId,
            visitId = visitId,
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
        Timber.d("Creating medication with visitId: $visitId, syncToNetwork: $syncToNetwork")
        localDataSource.upsert(medication.toLocal())
        if (syncToNetwork) {
            saveMedicationsToNetwork()
        }
        Timber.d("Saved medication to database: $name, visitId: $visitId, medicationId: $medicationId")
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
            val allMedications = localDataSource.getAll().toExternal()
            Timber.tag("MedicationRepository").d("All medications: $allMedications")
            val filteredMedications = allMedications.filter { it.visitId == visitId }
            Timber.tag("MedicationRepository").d("Filtered medications for visitId $visitId: $filteredMedications")
            filteredMedications
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteMedications = networkDataSource.loadMedications(userId)
            remoteMedications.forEach { med ->
                if (med.visitId == null) {
                    Timber.e("Invalid null visitId in Firebase for ${med.name}, id: ${med.medicationId}")
                    throw IllegalStateException("Firebase contains invalid data")
                }
            }
            Timber.tag("MedicationRepository").d("Remote medications: $remoteMedications")
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

    override suspend fun saveMedicationsToNetwork() {
        try {
            Timber.d("Syncing medications to network for userId: $userId, caller: ${Thread.currentThread().stackTrace[3]}")
            val localMedications = localDataSource.getAll()
            Timber.d("Local medications before sync: ${localMedications.map { "${it.name}: visitId=${it.visitId}" }}")
            val networkMedications = withContext(dispatcher) {
                localMedications.map { med ->
                    FirebaseMedication(
                        medicationId = med.medicationId,
                        userId = med.userId,
                        visitId = med.visitId,
                        name = med.name,
                        dosageUnit = med.dosageUnit,
                        dosageAmount = med.dosageAmount,
                        frequency = med.frequency,
                        timeOfDay = med.timeOfDay,
                        mealRelation = med.mealRelation,
                        startDate = med.startDate.toString(),
                        endDate = med.endDate.toString(),
                        notes = med.notes
                    ).also {
                        Timber.d("Mapping to network: ${it.name}, visitId=${it.visitId}")
                    }
                }
            }
            networkDataSource.saveMedications(networkMedications)
            Timber.d("Medications synced successfully to Firebase")

            // Cập nhật Room từ Firebase
            val firebaseMedications = networkDataSource.loadMedications(userId)
            Timber.d("Firebase medications loaded: ${firebaseMedications.map { "${it.name}: visitId=${it.visitId}" }}")
            val localMedIds = localMedications.map { it.medicationId }.toSet()
            firebaseMedications.forEach { firebaseMed ->
                val localMed = localMedications.find { it.medicationId == firebaseMed.medicationId }
                if (localMed != null) {
                    // Giữ visitId cục bộ nếu Firebase trả về null và local có visitId
                    val updatedVisitId = if (firebaseMed.visitId == null && localMed.visitId != null) {
                        Timber.w("Firebase medication ${firebaseMed.name} has null visitId, keeping local visitId: ${localMed.visitId}")
                        localMed.visitId
                    } else {
                        firebaseMed.visitId
                    }
                    localDataSource.upsert(
                        firebaseMed.copy(visitId = updatedVisitId).toLocal()
                    )
                    Timber.d("Updated Room with medication ${firebaseMed.name}, visitId: $updatedVisitId")
                } else {
                    // Thêm mới nếu không tồn tại trong Room
                    localDataSource.upsert(firebaseMed.toLocal())
                    Timber.d("Added new medication ${firebaseMed.name} to Room, visitId: ${firebaseMed.visitId}")
                }
            }
            localMedications.forEach { localMed ->
                if (localMed.medicationId !in firebaseMedications.map { it.medicationId }) {
                    Timber.w("Medication ${localMed.name} not found in Firebase, deleting from Room")
                    localDataSource.deleteById(localMed.medicationId)
                }
            }
            Timber.d("Room updated successfully from Firebase")
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync medications to network: ${e.message}")
            throw e
        }
    }
}