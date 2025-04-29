package com.example.healthcareproject.data.source.network.datasource

interface AuthDataSource {
    suspend fun loginUser(email: String, password: String)

    suspend fun registerUser(email: String, password: String)

    suspend fun googleSignIn(idToken: String)

    suspend fun resetPassword(email: String)

    suspend fun sendVerificationCode(email: String)

    suspend fun updatePassword(email: String, currentPassword: String, newPassword: String)

    suspend fun sendPasswordResetEmail(email: String)

    suspend fun logout()

    fun getCurrentUserId(): String?
}

