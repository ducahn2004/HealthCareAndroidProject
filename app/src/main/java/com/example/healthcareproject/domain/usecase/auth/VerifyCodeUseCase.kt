package com.example.healthcareproject.domain.usecase.auth

import com.example.healthcareproject.domain.repository.UserRepository
import javax.inject.Inject

class VerifyCodeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, code: String) {
        userRepository.verifyCode(email, code)
    }
}