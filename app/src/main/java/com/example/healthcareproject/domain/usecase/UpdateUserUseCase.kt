package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.domain.repository.UserRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

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
            val userFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val dbFormatter = DateTimeFormatter.ISO_DATE
            val date = LocalDate.parse(dateOfBirth, userFormatter)
            date.format(dbFormatter)
        } else {
            dateOfBirth
        }


        userRepository.updateUser(
            userId = userId,
            password = "", // Password is not updated here
            name = name,
            address = address,
            dateOfBirth = formattedDate,
            gender = gender,
            bloodType = bloodType,
            phone = phone
        )
    }
}