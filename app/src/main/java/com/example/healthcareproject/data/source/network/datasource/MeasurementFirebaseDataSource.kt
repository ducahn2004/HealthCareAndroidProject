package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseMeasurement
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class MeasurementFirebaseDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : MeasurementDataSource {

    private val measurementsRef = firebaseDatabase.getReference("measurements")

    override fun getMeasurementsFirebaseRealtime(userId: String): Flow<List<FirebaseMeasurement>>
            = callbackFlow {
        val ref = measurementsRef.child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull {
                    it.getValue(FirebaseMeasurement::class.java)
                }
                trySend(list).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun loadMeasurements(userId: String): List<FirebaseMeasurement> = try {
        measurementsRef
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .await()
            .children
            .mapNotNull { it.getValue(FirebaseMeasurement::class.java) }
    } catch (e: Exception) {
        throw Exception("Error loading measurements for userId '$userId': ${e.message}", e)
    }

    override suspend fun saveMeasurements(measurements: List<FirebaseMeasurement>) {
        if (measurements.isEmpty()) return

        try {
            val updates = measurements.associateBy { it.measurementId }
            measurementsRef.updateChildren(updates).await()
        } catch (e: Exception) {
            throw Exception("Error saving measurements: ${e.message}", e)
        }
    }
}