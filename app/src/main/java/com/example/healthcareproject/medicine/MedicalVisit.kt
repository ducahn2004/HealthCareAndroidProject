package com.example.healthcareproject.medicine

import java.util.Date

data class MedicalVisit(
    val id: String,
    val condition: String,
    val doctor: String,
    val facility: String,
    val time: String,
    val diagnosis: String,
    val doctorRemarks: String,
    val medications: List<Medication>
)