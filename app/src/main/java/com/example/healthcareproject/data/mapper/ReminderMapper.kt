package com.example.healthcareproject.data.mapper

import com.example.healthcareproject.data.source.local.entity.RoomReminder
import com.example.healthcareproject.data.source.network.model.FirebaseReminder
import com.example.healthcareproject.domain.model.Reminder
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// External to Local
fun Reminder.toLocal() = RoomReminder(
    reminderId = reminderId,
    userId = userId,
    title = title,
    message = message,
    reminderTime = reminderTime,
    repeatPattern = repeatPattern,
    status = status,
    createdAt = createdAt
)

fun List<Reminder>.toLocal() = map(Reminder::toLocal)

// Local to External
fun RoomReminder.toExternal() = Reminder(
    reminderId = reminderId,
    userId = userId,
    title = title,
    message = message,
    reminderTime = reminderTime,
    repeatPattern = repeatPattern,
    status = status,
    createdAt = createdAt
)

@JvmName("localToExternal")
fun List<RoomReminder>.toExternal() = map(RoomReminder::toExternal)

// Network to Local
fun FirebaseReminder.toLocal() = RoomReminder(
    reminderId = reminderId,
    userId = userId,
    title = title,
    message = message,
    reminderTime = LocalTime.parse(reminderTime, DateTimeFormatter.ISO_TIME),
    repeatPattern = repeatPattern,
    status = status,
    createdAt = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME)
)

@JvmName("networkToLocal")
fun List<FirebaseReminder>.toLocal() = map(FirebaseReminder::toLocal)

// Local to Network
fun RoomReminder.toNetwork() = FirebaseReminder(
    reminderId = reminderId,
    userId = userId,
    title = title,
    message = message,
    reminderTime = reminderTime.format(DateTimeFormatter.ISO_TIME),
    repeatPattern = repeatPattern,
    status = status,
    createdAt = createdAt.format(DateTimeFormatter.ISO_DATE_TIME)
)

fun List<RoomReminder>.toNetwork() = map(RoomReminder::toNetwork)

// External to Network
fun Reminder.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<Reminder>.toNetwork() = map(Reminder::toNetwork)

// Network to External
fun FirebaseReminder.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<FirebaseReminder>.toExternal() = map(FirebaseReminder::toExternal)