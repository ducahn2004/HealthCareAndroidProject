package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import com.example.healthcareproject.domain.repository.UserRepository
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String) {
        userRepository.sendPasswordResetEmail(email)
    }
}