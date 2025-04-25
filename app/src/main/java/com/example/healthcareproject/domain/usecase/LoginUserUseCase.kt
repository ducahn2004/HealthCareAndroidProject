package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val userFirebaseDataSource: UserFirebaseDataSource
) {
    suspend operator fun invoke(userId: String, password: String) {
        userFirebaseDataSource.loginUser(userId, password)
    }
}