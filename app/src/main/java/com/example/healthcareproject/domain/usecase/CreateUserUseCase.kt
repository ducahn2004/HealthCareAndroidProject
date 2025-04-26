package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import com.example.healthcareproject.data.source.network.model.FirebaseUser
import com.example.healthcareproject.domain.model.BloodType
import com.example.healthcareproject.domain.model.Gender
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val userFirebaseDataSource: UserFirebaseDataSource
) {
    suspend operator fun invoke(
        userId: String,
        password: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ) {
        try {
            // Create user in Firebase Authentication
            userFirebaseDataSource.createUser(userId, password)
            println("User created in Authentication: $userId")

            // Parse gender and blood type
            // Parse gender and blood type
            val genderEnum = try {
                Gender.valueOf(gender.replace(" ", "").replaceFirstChar { it.uppercase() })
            } catch (e: IllegalArgumentException) {
                throw Exception("Invalid gender value: $gender")
            }

            val bloodTypeEnum = try {
                BloodType.valueOf(bloodType.replace(" ", "").replaceFirstChar { it.uppercase() })
            } catch (e: IllegalArgumentException) {
                throw Exception("Invalid blood type value: $bloodType")
            }

            // Create FirebaseUser object with unescaped userId
            val user = FirebaseUser(
                userId = userId, // Unescaped email (e.g., aeo4051@gmail.com)
                name = name,
                address = address,
                dateOfBirth = dateOfBirth,
                gender = genderEnum,
                bloodType = bloodTypeEnum,
                phone = phone
            )
            println("FirebaseUser created with userId: ${user.userId}")
            // Save user to Realtime Database (escaping handled by UserFirebaseDataSource)
            userFirebaseDataSource.saveUser(user)
            println("User data saved for: $userId")
        } catch (e: Exception) {
            println("Error in CreateUserUseCase: ${e.message}")
            throw e
        }
    }
}