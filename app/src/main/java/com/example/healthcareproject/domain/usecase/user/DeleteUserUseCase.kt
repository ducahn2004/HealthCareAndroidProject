package com.example.healthcareproject.domain.usecase.user

import com.example.healthcareproject.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String) {
        userRepository.deleteUser(userId)
    }
}