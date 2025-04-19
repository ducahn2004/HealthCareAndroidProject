package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "users")
data class User(
    @PrimaryKey val userId: String,
    val password: String,
    val name: String,
    val address: String?,
    val dateOfBirth: LocalDate,
    val gender: Gender,
    val bloodType: BloodType,
    val phone: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

enum class Gender {
    Male,
    Female
}

enum class BloodType {
    A,
    B,
    AB,
    O
}




