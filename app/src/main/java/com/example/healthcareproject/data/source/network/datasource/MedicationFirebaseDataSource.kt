package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseMedication
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject


class MedicationFirebaseDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : MedicationDataSource {

    private val medicationRef = firebaseDatabase.getReference("medications")
    private val listeners = mutableListOf<ValueEventListener>()

    override suspend fun loadMedications(userId: String): List<FirebaseMedication> = try {
        val snapshot = medicationRef
            .child(userId)
            .get()
            .await()
        val medications = snapshot.children.mapNotNull { child ->
            val medication = child.getValue(FirebaseMedication::class.java)
            Timber.d("Loaded medication ${medication?.medicationId} with visitId: ${medication?.visitId}")
            medication
        }
        Timber.d("Loaded ${medications.size} medications for userId: $userId")
        medications
    } catch (e: Exception) {
        Timber.e(e, "Error loading medications for userId '$userId': ${e.message}")
        throw Exception("Error loading medications for userId '$userId': ${e.message}", e)
    }

    override suspend fun saveMedications(medications: List<FirebaseMedication>) {
        if (medications.isEmpty()) {
            Timber.d("No medications to save")
            return
        }
        try {
            val updates = medications.associate { medication ->
                Timber.d("Saving medication ${medication.medicationId} with visitId: ${medication.visitId}")
                val data = mapOf(
                    "medicationId" to medication.medicationId,
                    "userId" to medication.userId,
                    "visitId" to medication.visitId,
                    "name" to medication.name,
                    "dosageUnit" to medication.dosageUnit?.name,
                    "dosageAmount" to medication.dosageAmount,
                    "frequency" to medication.frequency,
                    "timeOfDay" to medication.timeOfDay,
                    "mealRelation" to medication.mealRelation?.name,
                    "startDate" to medication.startDate?.toString(),
                    "endDate" to medication.endDate?.toString(),
                    "notes" to medication.notes
                )
                "${medication.userId}/${medication.medicationId}" to data
            }
            medicationRef.updateChildren(updates).await()
            // Xác nhận dữ liệu sau khi lưu
            val savedMedications = loadMedications(medications.first().userId)
            Timber.d("After saving, loaded ${savedMedications.size} medications: ${savedMedications.map { "${it.name}: visitId=${it.visitId}" }}")
            if (savedMedications.size != medications.size) {
                Timber.e("Mismatch in saved medications: expected ${medications.size}, got ${savedMedications.size}")
            }
            medications.forEach { med ->
                if (med.medicationId !in savedMedications.map { it.medicationId }) {
                    Timber.e("Medication ${med.name} (ID: ${med.medicationId}) not saved to Firebase")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error saving medications: ${e.message}")
            throw Exception("Error saving medications: ${e.message}", e)
        }
    }

    override fun removeListeners() {
        listeners.forEach { medicationRef.removeEventListener(it) }
        listeners.clear()
        Timber.d("Removed all listeners from medicationRef, count: ${listeners.size}")
    }

    override suspend fun addSyncListener(userId: String, onDataChange: (List<FirebaseMedication>) -> Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medications = snapshot.children.mapNotNull { child ->
                    val medication = child.getValue(FirebaseMedication::class.java)
                    Timber.d("Sync listener: Medication ${medication?.medicationId} with visitId: ${medication?.visitId}")
                    medication
                }
                Timber.d("Firebase data changed: ${medications.size} medications for userId: $userId")
                onDataChange(medications)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Medication sync cancelled for userId: $userId")
            }
        }
        medicationRef.child(userId).addValueEventListener(listener)
        listeners.add(listener)
        Timber.d("Added sync listener for userId: $userId, listener count: ${listeners.size}, caller: ${Thread.currentThread().stackTrace[3]}")
    }
}