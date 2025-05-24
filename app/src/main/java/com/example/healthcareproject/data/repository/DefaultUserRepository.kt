package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.dao.UserDao
import com.example.healthcareproject.data.source.network.datasource.AuthDataSource
import com.example.healthcareproject.data.source.network.datasource.UserDataSource
import com.example.healthcareproject.di.DefaultDispatcher
import com.example.healthcareproject.domain.model.User
import com.example.healthcareproject.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultUserRepository @Inject constructor(
    private val networkDataSource: UserDataSource,
    private val localDataSource: UserDao,
    private val authDataSource: AuthDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : UserRepository {

    private val userId: String
        get() = authDataSource.getCurrentUserId() ?: throw Exception("User not logged in")

    override suspend fun createUser(
        userId: String, // Input email
        password: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ): String = withContext(dispatcher) {
        Timber.d("Creating user with email: $userId")
        val user = User(
            userId = "", // Will be updated with UID
            password = password,
            name = name,
            address = address,
            dateOfBirth = LocalDate.parse(dateOfBirth),
            gender = com.example.healthcareproject.domain.model.Gender.valueOf(gender),
            bloodType = com.example.healthcareproject.domain.model.BloodType.valueOf(bloodType),
            phone = phone,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        try {
            val uid = authDataSource.registerUser(userId, password) // Returns Firebase Auth UID
            val userWithUid = user.copy(userId = uid)
            networkDataSource.saveUser(userWithUid.toNetwork()) // Use UID, not email
            localDataSource.upsert(userWithUid.toLocal())
            uid
        } catch (e: Exception) {
            Timber.e(e, "Failed to create user with email: $userId")
            throw Exception("Cannot create user with email $userId: ${e.message}")
        }
    }

    override suspend fun updateUser(
        userId: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ) {
        val user = getUser()?.copy(
            name = name,
            address = address,
            dateOfBirth = LocalDate.parse(dateOfBirth),
            gender = com.example.healthcareproject.domain.model.Gender.valueOf(gender),
            bloodType = com.example.healthcareproject.domain.model.BloodType.valueOf(bloodType),
            phone = phone,
            updatedAt = LocalDateTime.now()
        ) ?: throw Exception("User (id $userId) not found")

        localDataSource.upsert(user.toLocal())
        networkDataSource.updateUser(userId, user.toNetwork())
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            try {
                val remoteUser = networkDataSource.loadUser(userId)
                if (remoteUser != null) {
                    Timber.d("Loaded user from network: $remoteUser")
                    localDataSource.upsert(remoteUser.toLocal())
                    Timber.d("User saved to local database: $remoteUser")
                } else {
                    Timber.e("No user data found for ID: $userId")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to refresh user data")
            }
        }
    }

    override fun getUserStream(): Flow<User?> {
        Timber.d("Streaming user with ID: $userId")
        return localDataSource.observeById(userId)
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun getUser(forceUpdate: Boolean): User? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(userId)?.toExternal()
    }

    override suspend fun verifyCode(email: String, code: String) = withContext(dispatcher) {
        Timber.d("Verifying code for email: $email")
        try {
            authDataSource.verifyCode(email, code)
        } catch (e: Exception) {
            Timber.e(e, "Failed to verify code for email: $email")
            throw Exception("Cannot verify code for email $email: ${e.message}")
        }
    }

    override suspend fun deleteUser() {
        localDataSource.deleteById(userId)
        networkDataSource.deleteUser(userId)
    }

    override suspend fun updatePassword(
        email: String,
        currentPassword: String,
        newPassword: String
    ) = withContext(dispatcher) {
        Timber.d("Updating password for email: $email")
        try {
            authDataSource.updatePassword(email, currentPassword, newPassword)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update password for email: $email")
            throw Exception("Cannot update password for email $email: ${e.message}")
        }
    }

    override suspend fun sendPasswordResetEmail(email: String) = withContext(dispatcher) {
        Timber.d("Sending password reset email to: $email")
        try {
            authDataSource.sendVerificationCode(email)
        } catch (e: Exception) {
            Timber.e(e, "Failed to send password reset email to: $email")
            throw Exception("Cannot send password reset email to $email: ${e.message}")
        }
    }

    override suspend fun resetPassword(email: String, newPassword: String) = withContext(dispatcher) {
        Timber.d("Resetting password for email: $email")
        try {
            authDataSource.resetPassword(email, newPassword)
        } catch (e: Exception) {
            Timber.e(e, "Failed to reset password for email: $email")
            throw Exception("Cannot reset password for email $email: ${e.message}")
        }
    }

    override suspend fun loginUser(
        email: String,
        password: String
    ): String = withContext(dispatcher) {
        Timber.d("Attempting to log in with email: $email")
        try {
            val authResult = FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .await() // Use await() for coroutine support
            val uid = authResult.user?.uid
                ?: throw Exception("Login failed: No user found after authentication")
            refresh()
            Timber.d("Login successful, UID: $uid")
            uid
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e(e, "Login failed: Account not registered")
            throw Exception("Account not found. Please register or try Google Sign-In.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Timber.e(e, "Login failed: Invalid email or password")
            throw Exception("Invalid email or password")
        } catch (e: Exception) {
            Timber.e(e, "Login failed: ${e.message}")
            throw Exception("Login failed: ${e.message}")
        }
    }

    override suspend fun sendVerificationCode(email: String) = withContext(dispatcher) {
        Timber.d("Sending verification code to: $email")
        try {
            authDataSource.sendVerificationCode(email)
        } catch (e: Exception) {
            Timber.e(e, "Failed to send verification code to: $email")
            throw Exception("Cannot send verification code to $email: ${e.message}")
        }
    }

    override suspend fun logoutUser() = withContext(dispatcher) {
        Timber.d("Logging out user")
        try {
            authDataSource.logout()
        } catch (e: Exception) {
            Timber.e(e, "Failed to logout user")
            throw Exception("Cannot logout user: ${e.message}")
        }
    }

    override suspend fun resetPassword(email: String) {
        TODO("Not yet implemented")
    }

    override fun getCurrentUserId(): String? {
        Timber.d("Getting current user ID")
        return authDataSource.getCurrentUserId()
    }

    override suspend fun sendVerificationEmail(email: String) {
        authDataSource.sendVerificationCode(email)
    }
}