package com.example.healthcareproject.domain.repository

import com.example.healthcareproject.domain.model.User
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Interface to the data layer for users.
 */
interface UserRepository {

    suspend fun createUser(
        email: String,
        password: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ): String

    suspend fun updateUser(
        userId: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    )

    suspend fun refresh()

    fun getUserStream(): Flow<User?>

    suspend fun verifyCode(email: String, code: String)

    suspend fun getUser(forceUpdate: Boolean = true): User?

    suspend fun deleteUser()

    suspend fun updatePassword(email: String, currentPassword: String, newPassword: String)

    suspend fun sendPasswordResetEmail(email: String)

    suspend fun resetPassword(email: String, newPassword: String)

    suspend fun loginUser(userId: String, password: String): String

    suspend fun sendVerificationCode(email: String)

    suspend fun logoutUser()

    suspend fun resetPassword(email: String)

    fun getCurrentUserId(): String?

    suspend fun sendVerificationEmail(email: String)

}