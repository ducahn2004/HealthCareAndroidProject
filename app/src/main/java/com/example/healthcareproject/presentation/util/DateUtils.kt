package com.example.healthcareproject.presentation.util

import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

fun Date.toLocalDate(): LocalDate {
    return toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
}