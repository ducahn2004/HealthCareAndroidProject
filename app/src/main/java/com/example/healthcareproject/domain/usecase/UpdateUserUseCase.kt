package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.domain.repository.UserRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Use case for updating a user's profile information.
 */
class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    /**
     * Updates the user's profile with the provided details.
     * @param userId The user's email.
     * @param name The user's name.
     * @param address The user's address (optional).
     * @param dateOfBirth The user's date of birth (format: dd/MM/yyyy).
     * @param gender The user's gender (e.g., "Male", "Female").
     * @param bloodType The user's blood type (e.g., "A_POSITIVE", "B_NEGATIVE").
     * @param phone The user's phone number.
     * @throws Exception if the operation fails (e.g., user not found, invalid data).
     */
    suspend operator fun invoke(
        userId: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ) {
        val formattedDate = if (dateOfBirth.isNotEmpty()) {
            try {
                val userFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val dbFormatter = DateTimeFormatter.ISO_LOCAL_DATE
                val date = LocalDate.parse(dateOfBirth, userFormatter)
                date.format(dbFormatter)
            } catch (e: Exception) {
                throw Exception("Invalid date of birth format: $dateOfBirth. Expected dd/MM/yyyy")
            }
        } else {
            dateOfBirth
        }

        userRepository.updateUser(
            userId = userId,
            name = name,
            address = address,
            dateOfBirth = formattedDate,
            gender = gender,
            bloodType = bloodType,
            phone = phone
        )
    }
}