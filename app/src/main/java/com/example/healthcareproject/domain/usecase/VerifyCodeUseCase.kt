package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import com.example.healthcareproject.domain.repository.UserRepository
import javax.inject.Inject

class VerifyCodeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, code: String) {
        userRepository.verifyCode(email, code)
    }
    suspend operator fun invoke(email: String) {
        userRepository.sendVerificationCode(email)
    }
}