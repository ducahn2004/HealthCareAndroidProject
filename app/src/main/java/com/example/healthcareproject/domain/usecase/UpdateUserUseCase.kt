package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.domain.repository.UserRepository
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
        userRepository.updateUser(
            userId = userId,
            password = "", // Password is not updated here
            name = name,
            address = address,
            dateOfBirth = dateOfBirth,
            gender = gender,
            bloodType = bloodType,
            phone = phone
        )
    }
}