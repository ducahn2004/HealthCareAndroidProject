package com.example.healthcareproject.data.repository

import androidx.room.withTransaction
import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.source.local.AppDatabase
import com.example.healthcareproject.data.source.local.dao.MedicalVisitDao
import com.example.healthcareproject.data.source.local.dao.MedicationDao
import com.example.healthcareproject.data.source.network.datasource.AuthDataSource
import com.example.healthcareproject.data.source.network.datasource.MedicationDataSource
import com.example.healthcareproject.data.source.network.model.FirebaseMedication
import com.example.healthcareproject.di.DefaultDispatcher
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.repository.MedicationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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
    private val medicalVisitDao: MedicalVisitDao,
    private val authDataSource: AuthDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    private val appDatabase: AppDatabase
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
        val currentMedication = getMedication(medicationId) ?: throw Exception("Medication (id $medicationId) not found")
        Timber.d("Current medication: name=${currentMedication.name}, id=$medicationId, visitId=${currentMedication.visitId}")
        val medication = currentMedication.copy(
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
        Timber.d("Updating medication: name=$name, id=$medicationId, visitId=${medication.visitId}")
        localDataSource.upsert(medication.toLocal())
        Timber.d("Upsert medication to Room: name=$name, id=$medicationId, visitId=${medication.visitId}")
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
            appDatabase.withTransaction {
                saveMedicationsToNetwork()
                val remoteMedication = networkDataSource.loadMedications(userId)
                localDataSource.upsertAll(remoteMedication.toLocal())
            }
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
        withContext(dispatcher) {
            try {
                Timber.d("Deleting medication $medicationId from Room")
                localDataSource.deleteById(medicationId)
                Timber.d("Deleting medication $medicationId from Firebase")
                networkDataSource.deleteMedication(medicationId)
                Timber.d("Medication $medicationId deleted successfully")
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete medication $medicationId")
                throw e
            }
        }
    }

    override suspend fun saveMedicationsToNetwork() {
        try {
            Timber.d("Syncing medications to network for userId: $userId, caller: ${Thread.currentThread().stackTrace[3]}")
            val localMedications = localDataSource.getAll()
            localMedications.forEach { med ->
                Timber.d("Local medication before sync: name=${med.name}, id=${med.medicationId}, visitId=${med.visitId}, userId=${med.userId}")
            }
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
                        Timber.d("Mapping to network: name=${it.name}, id=${it.medicationId}, visitId=${it.visitId}")
                    }
                }
            }
            Timber.d("Sending ${networkMedications.size} medications to Firebase")
            networkDataSource.saveMedications(networkMedications)
            Timber.d("Medications synced successfully to Firebase")


            val firebaseMedications = networkDataSource.loadMedications(userId)
            Timber.d("Before sync with Firebase: medications=${firebaseMedications.map { "${it.name}: visitId=${it.visitId}, id=${it.medicationId}" }}")
            firebaseMedications.forEach { med ->
                Timber.d("Firebase medication loaded: name=${med.name}, id=${med.medicationId}, visitId=${med.visitId}, userId=${med.userId}")
            }
            Timber.d("Firebase medications loaded: ${firebaseMedications.map { "${it.name}: visitId=${it.visitId}" }}")

            firebaseMedications.forEach { firebaseMed ->
                val localMed = localMedications.find { it.medicationId == firebaseMed.medicationId }
                if (localMed != null) {
                    val visitExists = medicalVisitDao.getMedicalVisitById(firebaseMed.visitId) != null
                    Timber.d("Checking RoomMedicalVisit for visitId=${firebaseMed.visitId}, exists=$visitExists")
                    Timber.d("Processing medication: name=${firebaseMed.name}, id=${firebaseMed.medicationId}, local visitId=${localMed.visitId}, firebase visitId=${firebaseMed.visitId}")
                    val updatedVisitId = if (firebaseMed.visitId == null && localMed.visitId != null) {
                        Timber.w("Firebase medication ${firebaseMed.name} has null visitId, keeping local visitId: ${localMed.visitId}")
                        localMed.visitId
                    } else {
                        Timber.d("Using Firebase visitId for ${firebaseMed.name}: ${firebaseMed.visitId}")
                        firebaseMed.visitId
                    }
                    localDataSource.upsert(
                        firebaseMed.copy(visitId = updatedVisitId).toLocal()
                    )
                    Timber.d("After sync to Room: medication ${firebaseMed.name}, id=${firebaseMed.medicationId}, updated visitId=${updatedVisitId}")
                } else {
                    localDataSource.upsert(firebaseMed.toLocal())
                    Timber.d("Added new medication ${firebaseMed.name} to Room, id=${firebaseMed.medicationId}, visitId=${firebaseMed.visitId}")
                }
            }
            localMedications.forEach { localMed ->
                if (localMed.medicationId !in firebaseMedications.map { it.medicationId }) {
                    Timber.w("Medication ${localMed.name} not found in Firebase, deleting from Room, id=${localMed.medicationId}")
                    localDataSource.deleteById(localMed.medicationId)
                }
            }
            Timber.d("Room after sync: medications=${localDataSource.getAll().map { "${it.name}: visitId=${it.visitId}, id=${it.medicationId}" }}")
            Timber.d("Room updated successfully from Firebase")

            val updatedLocalMedications = localDataSource.getAll()
            updatedLocalMedications.forEach { med ->
                Timber.d("Room after sync (verification): name=${med.name}, id=${med.medicationId}, visitId=${med.visitId}, userId=${med.userId}")
            }
            Timber.d("Room verification completed: ${updatedLocalMedications.size} medications, visitIds: ${updatedLocalMedications.map { "${it.name}: ${it.visitId}" }}")

        } catch (e: Exception) {
            Timber.e(e, "Failed to sync medications to network: ${e.message}")
            throw e
        }
    }
}