package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.domain.model.User
import com.example.healthcareproject.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun invoke(identifier: String, forceUpdate: Boolean = false, isUid: Boolean = false): User? {
        return if (isUid) {
            userRepository.getUserByUid(identifier, forceUpdate)
        } else {
            userRepository.getUser(identifier, forceUpdate)
        }
    }
}