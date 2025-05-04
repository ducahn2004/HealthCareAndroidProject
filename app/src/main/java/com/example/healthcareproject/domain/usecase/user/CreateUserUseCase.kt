package com.example.healthcareproject.domain.usecase.user

import com.example.healthcareproject.domain.repository.UserRepository
import timber.log.Timber
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ): String {
        Timber.Forest.d("Creating user with email: $email")
        try {
            // Validate inputs
            if (email.isBlank()) throw Exception("Email cannot be empty")
            if (password.length < 8) throw Exception("Password must be at least 8 characters")
            if (name.isBlank()) throw Exception("Name cannot be empty")
            if (dateOfBirth.isBlank()) throw Exception("Date of birth cannot be empty")
            if (phone.isBlank()) throw Exception("Phone number cannot be empty")

            // Call UserRepository to create the user
            val uid = userRepository.createUser(
                email = email,
                password = password,
                name = name,
                address = address,
                dateOfBirth = dateOfBirth,
                gender = gender,
                bloodType = bloodType,
                phone = phone
            )
            Timber.Forest.d("User created successfully with UID: $uid")
            return uid
        } catch (e: Exception) {
            Timber.Forest.e(e, "Failed to create user with email: $email")
            throw Exception("Failed to create user: ${e.message}", e)
        }
    }
}