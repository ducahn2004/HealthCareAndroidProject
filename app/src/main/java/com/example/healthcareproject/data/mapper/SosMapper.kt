package com.example.healthcareproject.data.mapper

import com.example.healthcareproject.data.source.local.entity.RoomSos
import com.example.healthcareproject.data.source.network.model.FirebaseSos
import com.example.healthcareproject.domain.model.Sos
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// External to Local
fun Sos.toLocal() = RoomSos(
    sosId = sosId,
    userId = userId,
    measurementId = measurementId,
    emergencyId = emergencyId,
    triggerReason = triggerReason,
    contacted = contacted,
    timestamp = timestamp
)

fun List<Sos>.toLocal() = map(Sos::toLocal)

// Local to External
fun RoomSos.toExternal() = Sos(
    sosId = sosId,
    userId = userId,
    measurementId = measurementId,
    emergencyId = emergencyId,
    triggerReason = triggerReason,
    contacted = contacted,
    timestamp = timestamp
)

@JvmName("localToExternal")
fun List<RoomSos>.toExternal() = map(RoomSos::toExternal)

// Network to Local
fun FirebaseSos.toLocal() = RoomSos(
    sosId = sosId,
    userId = userId,
    measurementId = measurementId,
    emergencyId = emergencyId,
    triggerReason = triggerReason,
    contacted = contacted,
    timestamp = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME)
)

@JvmName("networkToLocal")
fun List<FirebaseSos>.toLocal() = map(FirebaseSos::toLocal)

// Local to Network
fun RoomSos.toNetwork() = FirebaseSos(
    sosId = sosId,
    userId = userId,
    measurementId = measurementId,
    emergencyId = emergencyId,
    triggerReason = triggerReason,
    contacted = contacted,
    timestamp = timestamp.format(DateTimeFormatter.ISO_DATE_TIME)
)

fun List<RoomSos>.toNetwork() = map(RoomSos::toNetwork)

// External to Network
fun Sos.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<Sos>.toNetwork() = map(Sos::toNetwork)

// Network to External
fun FirebaseSos.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<FirebaseSos>.toExternal() = map(FirebaseSos::toExternal)