package com.example.healthcareproject.domain.repository

import com.example.healthcareproject.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the data layer for users.
 */
interface UserRepository {

    suspend fun createUser(
        userId: String,
        password: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    )

    suspend fun updateUser(
        userId: String,
        password: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    )

    suspend fun refresh(userId: String)

    fun getUserStream(userId: String): Flow<User?>

    suspend fun getUser(userId: String, forceUpdate: Boolean = false): User?

    suspend fun refreshUser(userId: String)

    suspend fun deleteUser(userId: String)

    suspend fun loginUser(email: String, password: String)

    suspend fun logoutUser()

    suspend fun resetPassword(email: String)

    fun getCurrentUserId(): String?

    suspend fun sendVerificationEmail(email: String)

}