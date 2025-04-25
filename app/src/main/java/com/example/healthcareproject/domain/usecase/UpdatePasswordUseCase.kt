package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val userFirebaseDataSource: UserFirebaseDataSource
) {
    suspend operator fun invoke(newPassword: String) {
        userFirebaseDataSource.updatePassword(newPassword)
    }
}