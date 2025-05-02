package com.example.healthcareproject.domain.usecase.user

import com.example.healthcareproject.domain.model.User
import com.example.healthcareproject.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(forceUpdate: Boolean = false): User? {
        return userRepository.getUser(forceUpdate)
    }
}