package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val userFirebaseDataSource: UserFirebaseDataSource
) {
    suspend operator fun invoke(email: String, newPassword: String) {
        try {
            userFirebaseDataSource.resetPassword(email, newPassword)
        } catch (e: Exception) {
            throw Exception("Password reset failed: ${e.message}", e)
        }
    }
}