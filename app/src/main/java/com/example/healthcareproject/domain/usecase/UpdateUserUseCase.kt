package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import com.example.healthcareproject.data.source.network.model.FirebaseUser
import com.example.healthcareproject.domain.model.BloodType
import com.example.healthcareproject.domain.model.Gender
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userFirebaseDataSource: UserFirebaseDataSource
) {
    suspend operator fun invoke(
        userId: String,
        name: String,
        address: String,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ) {
        try {
            val genderEnum = Gender.valueOf(gender.replace(" ", "").replaceFirstChar { it.uppercase() })
            val bloodTypeEnum = BloodType.valueOf(bloodType.replace(" ", "").replaceFirstChar { it.uppercase() })

            val user = FirebaseUser(
                userId = userId, // Unescaped email
                name = name,
                address = address,
                dateOfBirth = dateOfBirth,
                gender = genderEnum,
                bloodType = bloodTypeEnum,
                phone = phone
            )

            userFirebaseDataSource.updateUser(userId, user)
            println("User updated: $userId")
        } catch (e: Exception) {
            println("Error in UpdateUserUseCase: ${e.message}")
            throw e
        }
    }
}