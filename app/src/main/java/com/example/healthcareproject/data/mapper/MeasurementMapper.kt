package com.example.healthcareproject.data.mapper

import com.example.healthcareproject.data.source.local.entity.RoomMeasurement
import com.example.healthcareproject.data.source.network.model.FirebaseMeasurement
import com.example.healthcareproject.domain.model.Measurement

// External to Local
fun Measurement.toLocal() = RoomMeasurement(
    measurementId = measurementId,
    userId = userId,
    bpm = bpm,
    spO2 = spO2
)

fun List<Measurement>.toLocal() = map(Measurement::toLocal)

// Local to External
fun RoomMeasurement.toExternal() = Measurement(
    measurementId = measurementId,
    userId = userId,
    bpm = bpm,
    spO2 = spO2,
)

@JvmName("localToExternal")
fun List<RoomMeasurement>.toExternal() = map(RoomMeasurement::toExternal)

// Network to Local
fun FirebaseMeasurement.toLocal() = RoomMeasurement(
    measurementId = measurementId,
    userId = userId,
    bpm = bpm,
    spO2 = spO2,
)

@JvmName("networkToLocal")
fun List<FirebaseMeasurement>.toLocal() = map(FirebaseMeasurement::toLocal)

// Local to Network
fun RoomMeasurement.toNetwork() = FirebaseMeasurement(
    measurementId = measurementId,
    userId = userId,
    bpm = bpm,
    spO2 = spO2,
)

fun List<RoomMeasurement>.toNetwork() = map(RoomMeasurement::toNetwork)

// External to Network
fun Measurement.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<Measurement>.toNetwork() = map(Measurement::toNetwork)

// Network to External
fun FirebaseMeasurement.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<FirebaseMeasurement>.toExternal() = map(FirebaseMeasurement::toExternal)