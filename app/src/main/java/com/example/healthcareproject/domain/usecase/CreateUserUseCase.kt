package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.domain.repository.UserRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for creating a new user in the system.
 */
class CreateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    /**
     * Creates a new user with the provided details.
     * @param userId The user's email address.
     * @param password The user's password.
     * @param name The user's full name.
     * @param address The user's address (optional).
     * @param dateOfBirth The user's date of birth in ISO format (yyyy-MM-dd).
     * @param gender The user's gender (e.g., "MALE", "FEMALE").
     * @param bloodType The user's blood type (e.g., "A_POSITIVE", "B_NEGATIVE").
     * @param phone The user's phone number.
     * @return The unique identifier (UID) of the created user.
     * @throws Exception if the creation fails (e.g., invalid inputs, email already exists).
     */
    suspend operator fun invoke(
        userId: String,
        password: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ): String {
        Timber.d("Creating user with userId: $userId")
        try {
            // Validate inputs
            if (userId.isBlank()) throw Exception("Email cannot be empty")
            if (password.length < 8) throw Exception("Password must be at least 8 characters")
            if (name.isBlank()) throw Exception("Name cannot be empty")
            if (dateOfBirth.isBlank()) throw Exception("Date of birth cannot be empty")
            if (phone.isBlank()) throw Exception("Phone number cannot be empty")

            // Call UserRepository to create the user
            val uid = userRepository.createUser(
                userId = userId,
                password = password,
                name = name,
                address = address,
                dateOfBirth = dateOfBirth,
                gender = gender,
                bloodType = bloodType,
                phone = phone
            )
            Timber.d("User created successfully with UID: $uid")
            return uid
        } catch (e: Exception) {
            Timber.e(e, "Failed to create user with userId: $userId")
            throw Exception("Failed to create user: ${e.message}", e)
        }
    }
}