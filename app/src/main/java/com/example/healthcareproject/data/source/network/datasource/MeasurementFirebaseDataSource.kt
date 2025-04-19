package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.firebase.FirebaseService
import com.example.healthcareproject.data.source.network.model.FirebaseMeasurement
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class MeasurementFirebaseDataSource : MeasurementDataSource {

    private val measurementsRef = FirebaseService.getReference("measurements")

    override suspend fun writeMeasurement(measurement: FirebaseMeasurement) {
        measurementsRef.child(measurement.userId).child(measurement.measurementId)
            .setValue(measurement).await()
    }

    override suspend fun readMeasurements(userId: String): List<FirebaseMeasurement> {
        val snapshot = measurementsRef.child(userId).get().await()
        return snapshot.children.mapNotNull { it.getValue(FirebaseMeasurement::class.java) }
    }

    override suspend fun deleteMeasurement(userId: String, measurementId: String) {
        measurementsRef.child(userId).child(measurementId).removeValue().await()
    }

    override suspend fun deleteMeasurements(userId: String, measurementIds: List<String>) {
        measurementIds.map { measurementsRef.child(userId).child(it).removeValue() }
    }

    override suspend fun readAllMeasurementsByUserId(userId: String): List<FirebaseMeasurement> {
        val snapshot = measurementsRef.child(userId).get().await()
        return snapshot.children.mapNotNull { it.getValue(FirebaseMeasurement::class.java) }
    }

    override fun getMeasurementsRealtime(userId: String): Flow<List<FirebaseMeasurement>>
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
}