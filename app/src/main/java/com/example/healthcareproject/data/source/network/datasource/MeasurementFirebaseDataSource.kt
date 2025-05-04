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

    override fun getMeasurementsFirebaseRealtime(
        userId: String
    ): Flow<List<FirebaseMeasurement>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = mutableListOf<FirebaseMeasurement>()

                snapshot.children.forEach { deviceSnapshot ->
                    val deviceId = deviceSnapshot.key.orEmpty()

                    deviceSnapshot.children.forEach { measurementSnapshot ->
                        val data = measurementSnapshot.getValue(FirebaseMeasurement::class.java)
                        val userMatch = data?.userId == userId
                        if (data != null && userMatch) {
                            result += data.copy(
                                measurementId = measurementSnapshot.key.orEmpty(),
                                deviceId = deviceId
                            )
                        }
                    }
                }

                trySend(result).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        measurementsRef.addValueEventListener(listener)
        awaitClose { measurementsRef.removeEventListener(listener) }
    }

    override suspend fun loadMeasurements(userId: String): List<FirebaseMeasurement> = try {
        val snapshot = measurementsRef.get().await()
        val result = mutableListOf<FirebaseMeasurement>()

        snapshot.children.forEach { deviceSnapshot ->
            val deviceId = deviceSnapshot.key.orEmpty()

            deviceSnapshot.children.forEach { measurementSnapshot ->
                val data = measurementSnapshot.getValue(FirebaseMeasurement::class.java)
                val userMatch = data?.userId == userId
                if (data != null && userMatch) {
                    result += data.copy(
                        measurementId = measurementSnapshot.key.orEmpty(),
                        deviceId = deviceId
                    )
                }
            }
        }

        result
    } catch (e: Exception) {
        throw Exception("Error loading measurements: ${e.message}", e)
    }

    override suspend fun saveMeasurements(measurements: List<FirebaseMeasurement>) {
        if (measurements.isEmpty()) return

        try {
            val updates = mutableMapOf<String, Any?>()
            for (m in measurements) {
                val path = "${m.deviceId}/${m.measurementId}"
                updates[path] = mapOf(
                    "BPM" to m.bpm,
                    "SpO2" to m.spO2,
                    "userId" to m.userId
                )
            }

            measurementsRef.updateChildren(updates).await()
        } catch (e: Exception) {
            throw Exception("Error saving measurements: ${e.message}", e)
        }
    }
}
