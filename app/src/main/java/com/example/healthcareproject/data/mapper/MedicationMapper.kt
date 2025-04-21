package com.example.healthcareproject.data.mapper

import com.example.healthcareproject.data.source.local.entity.RoomMedication
import com.example.healthcareproject.data.source.network.model.FirebaseMedication
import com.example.healthcareproject.domain.model.Medication
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// External to Local
fun Medication.toLocal() = RoomMedication(
    medicationId = medicationId,
    userId = userId,
    visitId = visitId,
    name = name,
    dosageUnit = dosageUnit,
    dosageAmount = dosageAmount,
    frequency = frequency,
    timeOfDay = timeOfDay,
    mealRelation = mealRelation,
    startDate = startDate,
    endDate = endDate,
    notes = notes
)

fun List<Medication>.toLocal() = map(Medication::toLocal)

// Local to External
fun RoomMedication.toExternal() = Medication(
    medicationId = medicationId,
    userId = userId,
    visitId = visitId,
    name = name,
    dosageUnit = dosageUnit,
    dosageAmount = dosageAmount,
    frequency = frequency,
    timeOfDay = timeOfDay,
    mealRelation = mealRelation,
    startDate = startDate,
    endDate = endDate,
    notes = notes
)

@JvmName("localToExternal")
fun List<RoomMedication>.toExternal() = map(RoomMedication::toExternal)

// Network to Local
fun FirebaseMedication.toLocal() = RoomMedication(
    medicationId = medicationId,
    userId = userId,
    visitId = visitId,
    name = name,
    dosageUnit = dosageUnit,
    dosageAmount = dosageAmount,
    frequency = frequency,
    timeOfDay = timeOfDay,
    mealRelation = mealRelation,
    startDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE),
    endDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE),
    notes = notes
)

@JvmName("networkToLocal")
fun List<FirebaseMedication>.toLocal() = map(FirebaseMedication::toLocal)

// Local to Network
fun RoomMedication.toNetwork() = FirebaseMedication(
    medicationId = medicationId,
    userId = userId,
    visitId = visitId,
    name = name,
    dosageUnit = dosageUnit,
    dosageAmount = dosageAmount,
    frequency = frequency,
    timeOfDay = timeOfDay,
    mealRelation = mealRelation,
    startDate = startDate.format(DateTimeFormatter.ISO_DATE),
    endDate = endDate.format(DateTimeFormatter.ISO_DATE),
    notes = notes
)

fun List<RoomMedication>.toNetwork() = map(RoomMedication::toNetwork)

// External to Network
fun Medication.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<Medication>.toNetwork() = map(Medication::toNetwork)

// Network to External
fun FirebaseMedication.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<FirebaseMedication>.toExternal() = map(FirebaseMedication::toExternal)