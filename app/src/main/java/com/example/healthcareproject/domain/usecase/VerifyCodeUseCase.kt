package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import javax.inject.Inject

class VerifyCodeUseCase @Inject constructor(
    private val userFirebaseDataSource: UserFirebaseDataSource
) {
    suspend operator fun invoke(email: String, code: String) {
        userFirebaseDataSource.verifyCode(email, code)
    }
}