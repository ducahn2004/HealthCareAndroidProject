package com.example.healthcareproject.data.mapper

import com.example.healthcareproject.data.source.local.entity.RoomEmergencyInfo
import com.example.healthcareproject.data.source.network.model.FirebaseEmergencyInfo
import com.example.healthcareproject.domain.model.EmergencyInfo
import com.example.healthcareproject.domain.model.Relationship

// External to Local
fun EmergencyInfo.toLocal() = RoomEmergencyInfo(
    emergencyId = emergencyId,
    userId = userId,
    emergencyName = emergencyName,
    emergencyPhone = emergencyPhone,
    relationship = relationship,
    priority = priority
)

fun List<EmergencyInfo>.toLocal() = map(EmergencyInfo::toLocal)

// Local to External
fun RoomEmergencyInfo.toExternal() = EmergencyInfo(
    emergencyId = emergencyId,
    userId = userId,
    emergencyName = emergencyName,
    emergencyPhone = emergencyPhone,
    relationship = relationship,
    priority = priority
)

@JvmName("localToExternal")
fun List<RoomEmergencyInfo>.toExternal() = map(RoomEmergencyInfo::toExternal)

// Network to Local
fun FirebaseEmergencyInfo.toLocal() = RoomEmergencyInfo(
    emergencyId = emergencyId,
    userId = userId,
    emergencyName = emergencyName,
    emergencyPhone = emergencyPhone,
    relationship = Relationship.valueOf(relationship),
    priority = priority
)

@JvmName("networkToLocal")
fun List<FirebaseEmergencyInfo>.toLocal() = map(FirebaseEmergencyInfo::toLocal)

// Local to Network
fun RoomEmergencyInfo.toNetwork() = FirebaseEmergencyInfo(
    emergencyId = emergencyId,
    userId = userId,
    emergencyName = emergencyName,
    emergencyPhone = emergencyPhone,
    relationship = relationship.name,
    priority = priority
)

fun List<RoomEmergencyInfo>.toNetwork() = map(RoomEmergencyInfo::toNetwork)

// External to Network
fun EmergencyInfo.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<EmergencyInfo>.toNetwork() = map(EmergencyInfo::toNetwork)

// Network to External
fun FirebaseEmergencyInfo.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<FirebaseEmergencyInfo>.toExternal() = map(FirebaseEmergencyInfo::toExternal)