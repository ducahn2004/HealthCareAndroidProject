package com.example.healthcareproject.data.mapper

import com.example.healthcareproject.data.source.local.entity.RoomNotification
import com.example.healthcareproject.data.source.network.model.FirebaseNotification
import com.example.healthcareproject.domain.model.Notification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// External to Local
fun Notification.toLocal() = RoomNotification(
    notificationId = notificationId,
    userId = userId,
    type = type,
    relatedTable = relatedTable,
    relatedId = relatedId,
    message = message,
    timestamp = timestamp
)

fun List<Notification>.toLocal() = map(Notification::toLocal)

// Local to External
fun RoomNotification.toExternal() = Notification(
    notificationId = notificationId,
    userId = userId,
    type = type,
    relatedTable = relatedTable,
    relatedId = relatedId,
    message = message,
    timestamp = timestamp
)

@JvmName("localToExternal")
fun List<RoomNotification>.toExternal() = map(RoomNotification::toExternal)

// Network to Local
fun FirebaseNotification.toLocal() = RoomNotification(
    notificationId = notificationId,
    userId = userId,
    type = type,
    relatedTable = relatedTable,
    relatedId = relatedId,
    message = message,
    timestamp = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME)
)

@JvmName("networkToLocal")
fun List<FirebaseNotification>.toLocal() = map(FirebaseNotification::toLocal)

// Local to Network
fun RoomNotification.toNetwork() = FirebaseNotification(
    notificationId = notificationId,
    userId = userId,
    type = type,
    relatedTable = relatedTable,
    relatedId = relatedId,
    message = message,
    timestamp = timestamp.format(DateTimeFormatter.ISO_DATE_TIME)
)

fun List<RoomNotification>.toNetwork() = map(RoomNotification::toNetwork)

// External to Network
fun Notification.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<Notification>.toNetwork() = map(Notification::toNetwork)

// Network to External
fun FirebaseNotification.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<FirebaseNotification>.toExternal() = map(FirebaseNotification::toExternal)