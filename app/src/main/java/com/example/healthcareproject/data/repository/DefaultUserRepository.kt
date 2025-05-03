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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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
        email: String,
        password: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ): String = withContext(dispatcher) {
        Timber.d("Creating user with email: $email")
        if (email.isBlank()) throw Exception("Email cannot be empty")
        if (password.length < 6) throw Exception("Password must be at least 6 characters")

        val uid = authDataSource.registerUser(email, password)
        val user = User(
            userId = uid,
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
            networkDataSource.saveUser(user.toNetwork())
            localDataSource.upsert(user.toLocal())
            uid
        } catch (e: Exception) {
            Timber.e(e, "Failed to create user with email: $email")
            // Clean up: Delete the user from Firebase Authentication
            try {
                authDataSource.deleteUser(uid)
                Timber.d("Deleted user $uid from Authentication due to database failure")
            } catch (deleteException: Exception) {
                Timber.e(deleteException, "Failed to delete user $uid from Authentication")
            }
            throw Exception("Cannot create user with email $email: ${e.message}")
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
    ) = withContext(dispatcher) {
        Timber.d("Updating user with ID: $userId")
        val user = getUser()?.copy(
            name = name,
            address = address,
            dateOfBirth = LocalDate.parse(dateOfBirth),
            gender = com.example.healthcareproject.domain.model.Gender.valueOf(gender),
            bloodType = com.example.healthcareproject.domain.model.BloodType.valueOf(bloodType),
            phone = phone,
            updatedAt = java.time.LocalDateTime.now()
        ) ?: throw Exception("User (id $userId) not found")

        localDataSource.upsert(user.toLocal())
        networkDataSource.updateUser(userId, user.toNetwork())
    }

    override suspend fun refresh(userId: String) = withContext(dispatcher) {
        Timber.d("Refreshing user with ID: $userId")
        try {
            val uid = networkDataSource.getUidByEmail(userId)
                ?: throw Exception("UID not found for user ID $userId")
            val networkUser = networkDataSource.loadUser(uid)
                ?: throw Exception("User not found for ID $userId")
            localDataSource.upsert(networkUser.toLocal())
        } catch (e: Exception) {
            Timber.e(e, "Failed to refresh user: $userId")
            throw Exception("Cannot refresh user with ID $userId: ${e.message}")
        }
    }

    override fun getUserStream(userId: String): Flow<User?> {
        Timber.d("Streaming user with ID: $userId")
        return localDataSource.observeById(userId)
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun getUser(forceUpdate: Boolean): User? = withContext(dispatcher) {
        Timber.d("Getting current user, forceUpdate: $forceUpdate")
        val currentUserId = authDataSource.getCurrentUserId()
            ?: return@withContext null

        if (forceUpdate) {
            try {
                val uid = networkDataSource.getUidByEmail(currentUserId)
                    ?: throw Exception("UID not found for user ID $currentUserId")
                val networkUser = networkDataSource.loadUser(uid)
                    ?: throw Exception("User not found for ID $currentUserId")
                localDataSource.upsert(networkUser.toLocal())
            } catch (e: Exception) {
                Timber.e(e, "Failed to refresh current user: $currentUserId")
                throw Exception("Cannot refresh current user with ID $currentUserId: ${e.message}")
            }
        }

        return@withContext localDataSource.getById(currentUserId)?.toExternal()
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


    override suspend fun getUserByUid(uid: String, forceUpdate: Boolean): User? = withContext(dispatcher) {
        Timber.d("Getting user by UID: $uid, forceUpdate: $forceUpdate")
        if (forceUpdate) {
            try {
                val networkUser = networkDataSource.loadUser(uid)
                    ?: throw Exception("User not found for UID $uid")
                localDataSource.upsert(networkUser.toLocal())
            } catch (e: Exception) {
                Timber.e(e, "Failed to load user by UID: $uid")
                throw Exception("Cannot load user with UID $uid: ${e.message}")
            }
        }
        val email = networkDataSource.getEmailByUid(uid)
            ?: throw Exception("Email not found for UID $uid")
        localDataSource.getById(email)?.toExternal()
    }

    override suspend fun refreshUser(userId: String) {
        refresh(userId)
    }

    override suspend fun deleteUser(userId: String) = withContext(dispatcher) {
        Timber.d("Deleting user with ID: $userId")
        try {
            val uid = networkDataSource.getUidByEmail(userId)
                ?: throw Exception("UID not found for user ID $userId")
            localDataSource.deleteById(userId)
            networkDataSource.deleteUser(uid)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete user: $userId")
            throw Exception("Cannot delete user with ID $userId: ${e.message}")
        }
    }

    override suspend fun updatePassword(email: String, currentPassword: String, newPassword: String) = withContext(dispatcher) {
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

    override suspend fun loginUser(userId: String, password: String): String = withContext(dispatcher) {
        Timber.d("Logging in user with ID: $userId")
        try {
            authDataSource.loginUser(userId, password)
        } catch (e: Exception) {
            Timber.e(e, "Failed to login user: $userId")
            throw Exception("Cannot login user with ID $userId: ${e.message}")
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