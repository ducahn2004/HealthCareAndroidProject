package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    suspend fun getUidByEmail(email: String): String?

    suspend fun getEmailByUid(uid: String): String?

    suspend fun saveUser(user: FirebaseUser, uid: String)

    suspend fun loadUser(uid: String): FirebaseUser?

    suspend fun deleteUser(uid: String)

    suspend fun updateUser(uid: String, user: FirebaseUser)

    suspend fun verifyCode(email: String, code: String)

    suspend fun createUser(email: String, password: String): String

    suspend fun loginUser(userId: String, password: String)

    suspend fun googleSignIn(idToken: String)

    suspend fun updatePassword(email: String, currentPassword: String, newPassword: String)

    suspend fun resetPassword(email: String, newPassword: String)

    suspend fun sendPasswordResetEmail(email: String)

    suspend fun sendVerificationCode(email: String)
}