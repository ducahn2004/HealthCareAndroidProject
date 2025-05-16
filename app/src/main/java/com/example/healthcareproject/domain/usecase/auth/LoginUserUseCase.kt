package com.example.healthcareproject.domain.usecase.auth

import com.example.healthcareproject.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import timber.log.Timber
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    /**
     * Logs in a user with the provided email and password.
     * @param email The user's email.
     * @param password The user's password.
     * @return The UID of the authenticated user.
     * @throws FirebaseAuthInvalidUserException if the account is not registered.
     * @throws Exception for other login failures (e.g., invalid credentials, network error).
     */
    suspend operator fun invoke(email: String, password: String): String {
        Timber.d("Executing LoginUserUseCase for email: $email")
        try {
            val uid = userRepository.loginUser(email, password)
            Timber.d("Login successful, UID: $uid")
            return uid
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e(e, "Login failed: Account not registered")
            throw e // Propagate for ViewModel to handle
        } catch (e: Exception) {
            Timber.e(e, "Login failed: ${e.message}")
            throw e
        }
    }
}