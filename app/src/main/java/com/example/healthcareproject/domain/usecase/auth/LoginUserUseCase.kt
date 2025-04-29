package com.example.healthcareproject.domain.usecase.auth

import com.example.healthcareproject.domain.repository.UserRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    /**
     * Logs in a user with the provided email and password.
     * @param userId The user's email.
     * @param password The user's password.
     * @return The UID of the authenticated user.
     * @throws Exception if the login fails (e.g., invalid credentials, network error).
     */
    suspend operator fun invoke(userId: String, password: String): String {
        return userRepository.loginUser(userId, password)
    }
}