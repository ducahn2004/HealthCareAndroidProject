package com.example.healthcareproject.domain.usecase.auth

import com.example.healthcareproject.domain.repository.UserRepository
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, currentPassword: String, newPassword: String) {
        try {
            userRepository.updatePassword(email, currentPassword, newPassword)
        } catch (e: Exception) {
            throw Exception("Password update failed: ${e.message}", e)
        }
    }
}