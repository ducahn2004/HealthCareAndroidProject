package com.example.healthcareproject.data.mapper

import com.example.healthcareproject.data.source.local.entity.RoomAlert
import com.example.healthcareproject.data.source.network.model.FirebaseAlert
import com.example.healthcareproject.domain.model.Alert
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// External to Local
fun Alert.toLocal() = RoomAlert(
    alertId = alertId,
    userId = userId,
    title = title,
    message = message,
    alertTime = alertTime,
    repeatPattern = repeatPattern,
    status = status,
    createdAt = createdAt
)

fun List<Alert>.toLocal() = map(Alert::toLocal)

// Local to External
fun RoomAlert.toExternal() = Alert(
    alertId = alertId,
    userId = userId,
    title = title,
    message = message,
    alertTime = alertTime,
    repeatPattern = repeatPattern,
    status = status,
    createdAt = createdAt
)

@JvmName("localToExternal")
fun List<RoomAlert>.toExternal() = map(RoomAlert::toExternal)

// Network to Local
fun FirebaseAlert.toLocal() = RoomAlert(
    alertId = alertId,
    userId = userId,
    title = title,
    message = message,
    alertTime = LocalTime.parse(alertTime, DateTimeFormatter.ISO_TIME),
    repeatPattern = repeatPattern,
    status = status,
    createdAt = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME)
)

@JvmName("networkToLocal")
fun List<FirebaseAlert>.toLocal() = map(FirebaseAlert::toLocal)

// Local to Network
fun RoomAlert.toNetwork() = FirebaseAlert(
    alertId = alertId,
    userId = userId,
    title = title,
    message = message,
    alertTime = alertTime.format(DateTimeFormatter.ISO_TIME),
    repeatPattern = repeatPattern,
    status = status,
    createdAt = createdAt.format(DateTimeFormatter.ISO_DATE_TIME)
)

fun List<RoomAlert>.toNetwork() = map(RoomAlert::toNetwork)

// External to Network
fun Alert.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<Alert>.toNetwork() = map(Alert::toNetwork)

// Network to External
fun FirebaseAlert.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<FirebaseAlert>.toExternal() = map(FirebaseAlert::toExternal)