package com.example.healthcareproject.domain.usecase.user

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