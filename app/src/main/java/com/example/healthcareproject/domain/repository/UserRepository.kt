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
    ): String

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

    suspend fun verifyCode(email: String, code: String)

    suspend fun getUser(userId: String, forceUpdate: Boolean = false): User?

    suspend fun getUserByUid(uid: String, forceUpdate: Boolean): User?

    suspend fun refreshUser(userId: String)

    suspend fun deleteUser(userId: String)

}