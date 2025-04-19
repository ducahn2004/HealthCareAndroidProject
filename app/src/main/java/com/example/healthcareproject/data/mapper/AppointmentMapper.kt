package com.example.healthcareproject.data.mapper

import com.example.healthcareproject.data.source.local.entity.RoomAppointment
import com.example.healthcareproject.data.source.network.model.FirebaseAppointment
import com.example.healthcareproject.domain.model.Appointment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// External to Local
fun Appointment.toLocal() = RoomAppointment(
    appointmentId = appointmentId,
    userId = userId,
    visitId = visitId,
    doctorName = doctorName,
    location = location,
    appointmentTime = appointmentTime,
    note = note
)

fun List<Appointment>.toLocal() = map(Appointment::toLocal)

// Local to External
fun RoomAppointment.toExternal() = Appointment(
    appointmentId = appointmentId,
    userId = userId,
    visitId = visitId,
    doctorName = doctorName,
    location = location,
    appointmentTime = appointmentTime,
    note = note
)

@JvmName("localToExternal")
fun List<RoomAppointment>.toExternal() = map(RoomAppointment::toExternal)

// Network to Local
fun FirebaseAppointment.toLocal() = RoomAppointment(
    appointmentId = appointmentId,
    userId = userId,
    visitId = visitId,
    doctorName = doctorName,
    location = location,
    appointmentTime = LocalDateTime.parse(appointmentTime, DateTimeFormatter.ISO_DATE_TIME),
    note = note
)

@JvmName("networkToLocal")
fun List<FirebaseAppointment>.toLocal() = map(FirebaseAppointment::toLocal)

// Local to Network
fun RoomAppointment.toNetwork() = FirebaseAppointment(
    appointmentId = appointmentId,
    userId = userId,
    visitId = visitId,
    doctorName = doctorName,
    location = location,
    appointmentTime = appointmentTime.format(DateTimeFormatter.ISO_DATE_TIME),
    note = note
)

fun List<RoomAppointment>.toNetwork() = map(RoomAppointment::toNetwork)

// External to Network
fun Appointment.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<Appointment>.toNetwork() = map(Appointment::toNetwork)

// Network to External
fun FirebaseAppointment.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<FirebaseAppointment>.toExternal() = map(FirebaseAppointment::toExternal)