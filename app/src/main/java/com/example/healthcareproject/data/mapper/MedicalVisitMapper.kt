package com.example.healthcareproject.data.mapper

import com.example.healthcareproject.data.source.local.entity.RoomMedicalVisit
import com.example.healthcareproject.data.source.network.model.FirebaseMedicalVisit
import com.example.healthcareproject.domain.model.MedicalVisit
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// External to Local
fun MedicalVisit.toLocal() = RoomMedicalVisit(
    visitId = visitId,
    userId = userId,
    visitDate = visitDate,
    clinicName = clinicName,
    doctorName = doctorName,
    diagnosis = diagnosis,
    treatment = treatment,
    createdAt = createdAt
)

fun List<MedicalVisit>.toLocal() = map(MedicalVisit::toLocal)

// Local to External
fun RoomMedicalVisit.toExternal() = MedicalVisit(
    visitId = visitId,
    userId = userId,
    visitDate = visitDate,
    clinicName = clinicName,
    doctorName = doctorName,
    diagnosis = diagnosis,
    treatment = treatment,
    createdAt = createdAt
)

@JvmName("localToExternal")
fun List<RoomMedicalVisit>.toExternal() = map(RoomMedicalVisit::toExternal)

// Network to Local
fun FirebaseMedicalVisit.toLocal() = RoomMedicalVisit(
    visitId = medicalVisitId,
    userId = userId,
    visitDate = LocalDate.parse(visitDate, DateTimeFormatter.ISO_DATE),
    clinicName = clinicName,
    doctorName = doctorName,
    diagnosis = diagnosis,
    treatment = treatment,
    createdAt = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME)
)

@JvmName("networkToLocal")
fun List<FirebaseMedicalVisit>.toLocal() = map(FirebaseMedicalVisit::toLocal)

// Local to Network
fun RoomMedicalVisit.toNetwork() = FirebaseMedicalVisit(
    medicalVisitId = visitId,
    userId = userId,
    visitDate = visitDate.format(DateTimeFormatter.ISO_DATE),
    clinicName = clinicName,
    doctorName = doctorName,
    diagnosis = diagnosis,
    treatment = treatment,
    createdAt = createdAt.format(DateTimeFormatter.ISO_DATE_TIME)
)

fun List<RoomMedicalVisit>.toNetwork() = map(RoomMedicalVisit::toNetwork)

// External to Network
fun MedicalVisit.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<MedicalVisit>.toNetwork() = map(MedicalVisit::toNetwork)

// Network to External
fun FirebaseMedicalVisit.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<FirebaseMedicalVisit>.toExternal() = map(FirebaseMedicalVisit::toExternal)