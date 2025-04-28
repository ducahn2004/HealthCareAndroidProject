package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.domain.repository.UserRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor (
    private val userRepository: UserRepository
){
    suspend operator fun invoke(email: String, newPassword: String) {
        try {
            userRepository.resetPassword(email, newPassword)
        } catch (e: Exception) {
            throw Exception("Password reset failed: ${e.message}", e)
        }
    }

}