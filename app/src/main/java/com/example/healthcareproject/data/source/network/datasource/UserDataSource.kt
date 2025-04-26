package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    suspend fun saveUser(user: FirebaseUser)

    suspend fun loadUser(userId: String): FirebaseUser?

    suspend fun deleteUser(userId: String)

    suspend fun updateUser(userId: String, user: FirebaseUser)

    suspend fun verifyCode(email: String, code: String)

    suspend fun createUser(userId: String, password: String)

    suspend fun loginUser(userId: String, password: String)

    suspend fun googleSignIn(idToken: String)

    suspend fun updatePassword(newPassword: String)

    suspend fun updatePassword(userId: String, currentPassword: String, newPassword: String)

    suspend fun sendPasswordResetEmail(email: String)
}