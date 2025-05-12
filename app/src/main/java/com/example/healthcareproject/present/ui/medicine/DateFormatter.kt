package com.example.healthcareproject.present.ui.medicine

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateFormatter {
    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        @JvmStatic
        fun formatDate(date: LocalDate?): String {
            return date?.format(dateFormatter) ?: ""
        }

        @JvmStatic
        fun formatDate(dateTime: LocalDateTime?): String {
            return dateTime?.toLocalDate()?.format(dateFormatter) ?: ""
        }

        @JvmStatic
        fun formatTime(dateTime: LocalDateTime?): String {
            return dateTime?.format(timeFormatter) ?: ""
        }
    }
}