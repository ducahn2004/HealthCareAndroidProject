package com.example.healthcareproject.data.mapper

import com.example.healthcareproject.data.source.local.entity.RoomUser
import com.example.healthcareproject.data.source.network.model.FirebaseUser
import com.example.healthcareproject.domain.model.User
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// External to Local
fun User.toLocal() = RoomUser(
    userId = userId,
    password = password,
    name = name,
    address = address,
    dateOfBirth = dateOfBirth,
    gender = gender,
    bloodType = bloodType,
    phone = phone,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun List<User>.toLocal() = map(User::toLocal)

// Local to External
fun RoomUser.toExternal() = User(
    userId = userId,
    password = password,
    name = name,
    address = address,
    dateOfBirth = dateOfBirth,
    gender = gender,
    bloodType = bloodType,
    phone = phone,
    createdAt = createdAt,
    updatedAt = updatedAt
)

@JvmName("localToExternal")
fun List<RoomUser>.toExternal() = map(RoomUser::toExternal)

// Network to Local
fun FirebaseUser.toLocal() = RoomUser(
    userId = userId,
    password = password,
    name = name,
    address = address,
    dateOfBirth = LocalDate.parse(dateOfBirth, DateTimeFormatter.ISO_DATE),
    gender = gender,
    bloodType = bloodType,
    phone = phone,
    createdAt = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME),
    updatedAt = LocalDateTime.parse(updatedAt, DateTimeFormatter.ISO_DATE_TIME)
)

@JvmName("networkToLocal")
fun List<FirebaseUser>.toLocal() = map(FirebaseUser::toLocal)

// Local to Network
fun RoomUser.toNetwork() = FirebaseUser(
    userId = userId,
    password = password,
    name = name,
    address = address,
    dateOfBirth = dateOfBirth.format(DateTimeFormatter.ISO_DATE),
    gender = gender,
    bloodType = bloodType,
    phone = phone,
    createdAt = createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
    updatedAt = updatedAt.format(DateTimeFormatter.ISO_DATE_TIME)
)

fun List<RoomUser>.toNetwork() = map(RoomUser::toNetwork)

// External to Network
fun User.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<User>.toNetwork() = map(User::toNetwork)

// Network to External
fun FirebaseUser.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<FirebaseUser>.toExternal() = map(FirebaseUser::toExternal)