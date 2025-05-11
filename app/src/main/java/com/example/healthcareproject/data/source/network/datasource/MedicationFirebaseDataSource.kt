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
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .await()
        val medications = snapshot.children.mapNotNull { child ->
            val medication = child.getValue(FirebaseMedication::class.java)
            if (medication?.visitId == null && medication?.medicationId != null) {
                Timber.w("Medication ${medication.medicationId} has null visitId in Firebase")
            }
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
            // Tạo updates với ánh xạ rõ ràng
            val updates = medications.associate { medication ->
                // Kiểm tra visitId
                if (medication.visitId == null && medication.medicationId != null) {
                    Timber.w("Saving medication ${medication.medicationId} with null visitId")
                }
                // Ánh xạ thủ công để đảm bảo tất cả trường được lưu
                val data = mapOf(
                    "medicationId" to medication.medicationId,
                    "userId" to medication.userId,
                    "visitId" to medication.visitId, // Đảm bảo lưu visitId
                    "name" to medication.name,
                    "dosageUnit" to medication.dosageUnit?.name,
                    "dosageAmount" to medication.dosageAmount,
                    "frequency" to medication.frequency,
                    "timeOfDay" to medication.timeOfDay,
                    "mealRelation" to medication.mealRelation?.name,
                    "startDate" to medication.startDate?.toString(),
                    "endDate" to medication.endDate?.toString(),
                    "notes" to medication.notes,
                    "updatedAt" to LocalDateTime.now().toString()
                )
                medication.medicationId to data
            }
            medicationRef.updateChildren(updates).await()
            Timber.d("Saved ${medications.size} medications to Firebase")
        } catch (e: Exception) {
            Timber.e(e, "Error saving medications: ${e.message}")
            throw Exception("Error saving medications: ${e.message}", e)
        }
    }

    // Phương thức để gỡ listener nếu có
    override suspend fun removeListeners() {
        listeners.forEach { medicationRef.removeEventListener(it) }
        listeners.clear()
        Timber.d("Removed all listeners from medicationRef")
    }

    // Phương thức để thêm listener nếu cần đồng bộ liên tục
    override suspend fun addSyncListener(userId: String, onDataChange: (List<FirebaseMedication>) -> Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medications = snapshot.children.mapNotNull { child ->
                    val medication = child.getValue(FirebaseMedication::class.java)
                    if (medication?.visitId == null && medication?.medicationId != null) {
                        Timber.w("Firebase medication ${medication.medicationId} has null visitId")
                    }
                    medication
                }
                Timber.d("Firebase data changed: ${medications.size} medications for userId: $userId")
                onDataChange(medications)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Medication sync cancelled for userId: $userId")
            }
        }
        medicationRef.orderByChild("userId").equalTo(userId).addValueEventListener(listener)
        listeners.add(listener)
        Timber.d("Added sync listener for userId: $userId")
    }
}