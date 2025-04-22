package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.domain.model.User
import com.example.healthcareproject.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, forceUpdate: Boolean = false): User? {
        return userRepository.getUser(userId, forceUpdate)
    }
}