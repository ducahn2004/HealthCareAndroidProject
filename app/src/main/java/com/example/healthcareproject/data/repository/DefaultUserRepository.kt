package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.dao.UserDao
import com.example.healthcareproject.data.source.network.datasource.NetworkDataSourceError
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

/**
 * Sealed class for repository errors.
 */
sealed class RepositoryError : Exception() {
    data class UserNotFound(val userId: String) : RepositoryError()
    data class NetworkError(val message: String) : RepositoryError()
    data class InvalidInput(val message: String) : RepositoryError()
    data class Unauthorized(val message: String) : RepositoryError()
}

@Singleton
class DefaultUserRepository @Inject constructor(
    private val networkDataSource: UserDataSource,
    private val localDataSource: UserDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : UserRepository {

    override suspend fun createUser(
        userId: String,
        password: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ): String = withContext(dispatcher) {
        Timber.d("Creating user with ID: $userId")
        if (userId.isBlank()) throw RepositoryError.InvalidInput("User ID cannot be empty")
        if (password.length < 6) throw RepositoryError.InvalidInput("Password must be at least 6 characters")

        val generatedUserId = userId
        val user = User(
            userId = generatedUserId,
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
            // Giả định UID đã được tạo trước (e.g., qua Firebase Authentication)
            val uid = networkDataSource.getUidByEmail(userId)
                ?: throw RepositoryError.UserNotFound("UID not found for $userId")
            networkDataSource.saveUser(uid, user.toNetwork())
            localDataSource.upsert(user.toLocal())
            generatedUserId
        } catch (e: NetworkDataSourceError) {
            Timber.e(e, "Failed to create user: $userId")
            when (e) {
                is NetworkDataSourceError.NotFound -> throw RepositoryError.UserNotFound(userId)
                is NetworkDataSourceError.NetworkFailure -> throw RepositoryError.NetworkError(e.message)
                is NetworkDataSourceError.InvalidData -> throw RepositoryError.InvalidInput(e.message)
            }
        }
    }

    override suspend fun updateUser(
        userId: String,
        password: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ) = withContext(dispatcher) {
        Timber.d("Updating user with ID: $userId")
        val user = getUser(userId)?.copy(
            password = password,
            name = name,
            address = address,
            dateOfBirth = LocalDate.parse(dateOfBirth),
            gender = com.example.healthcareproject.domain.model.Gender.valueOf(gender),
            bloodType = com.example.healthcareproject.domain.model.BloodType.valueOf(bloodType),
            phone = phone,
            updatedAt = LocalDateTime.now()
        ) ?: throw RepositoryError.UserNotFound(userId)

        try {
            val uid = networkDataSource.getUidByEmail(userId)
                ?: throw RepositoryError.UserNotFound("UID not found for $userId")
            localDataSource.upsert(user.toLocal())
            networkDataSource.updateUser(uid, user.toNetwork())
        } catch (e: NetworkDataSourceError) {
            Timber.e(e, "Failed to update user: $userId")
            when (e) {
                is NetworkDataSourceError.NotFound -> throw RepositoryError.UserNotFound(userId)
                is NetworkDataSourceError.NetworkFailure -> throw RepositoryError.NetworkError(e.message)
                is NetworkDataSourceError.InvalidData -> throw RepositoryError.InvalidInput(e.message)
            }
        }
    }

    override suspend fun refresh(userId: String) = withContext(dispatcher) {
        Timber.d("Refreshing user with ID: $userId")
        try {
            val uid = networkDataSource.getUidByEmail(userId)
                ?: throw RepositoryError.UserNotFound("UID not found for $userId")
            val networkUser = networkDataSource.loadUser(uid)
                ?: throw RepositoryError.UserNotFound(userId)
            localDataSource.upsert(networkUser.toLocal())
        } catch (e: NetworkDataSourceError) {
            Timber.e(e, "Failed to refresh user: $userId")
            when (e) {
                is NetworkDataSourceError.NotFound -> throw RepositoryError.UserNotFound(userId)
                is NetworkDataSourceError.NetworkFailure -> throw RepositoryError.NetworkError(e.message)
                is NetworkDataSourceError.InvalidData -> throw RepositoryError.InvalidInput(e.message)
            }
        }
    }

    override fun getUserStream(userId: String): Flow<User?> {
        Timber.d("Streaming user with ID: $userId")
        return localDataSource.observeById(userId)
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun verifyCode(email: String, code: String) {
        Timber.d("Verifying code for email: $email")
        throw RepositoryError.InvalidInput("Verification not supported")
    }

    override suspend fun getUser(userId: String, forceUpdate: Boolean): User? = withContext(dispatcher) {
        Timber.d("Getting user with ID: $userId, forceUpdate: $forceUpdate")
        if (forceUpdate) {
            refresh(userId)
        }
        localDataSource.getById(userId)?.toExternal()
    }

    override suspend fun getUserByUid(uid: String, forceUpdate: Boolean): User? = withContext(dispatcher) {
        Timber.d("Getting user by UID: $uid, forceUpdate: $forceUpdate")
        if (forceUpdate) {
            try {
                val networkUser = networkDataSource.loadUser(uid)
                    ?:  ?: throw RepositoryError.UserNotFound("User not found for UID $uid")
                localDataSource.upsert(networkUser.toLocal())
            } catch (e: NetworkDataSourceError) {
                Timber.e(e, "Failed to load user by UID: $uid")
                when (e) {
                    is NetworkDataSourceError.NotFound -> throw RepositoryError.UserNotFound(uid)
                    is NetworkDataSourceError.NetworkFailure -> throw RepositoryError.NetworkError(e.message)
                    is NetworkDataSourceError.InvalidData -> throw RepositoryError.InvalidInput(e.message)
                }
            }
        }
        val email = networkDataSource.getEmailByUid(uid)
            ?: throw RepositoryError.UserNotFound("Email not found for UID $uid")
        localDataSource.getById(email)?.toExternal()
    }

    override suspend fun refreshUser(userId: String) {
        refresh(userId)
    }

    override suspend fun deleteUser(userId: String) = withContext(dispatcher) {
        Timber.d("Deleting user with ID: $userId")
        try {
            val uid = networkDataSource.getUidByEmail(userId)
                ?: throw RepositoryError.UserNotFound("UID not found for $userId")
            localDataSource.deleteById(userId)
            networkDataSource.deleteUser(uid)
        } catch (e: NetworkDataSourceError) {
            Timber.e(e, "Failed to delete user: $userId")
            when (e) {
                is NetworkDataSourceError.NotFound -> throw RepositoryError.UserNotFound(userId)
                is NetworkDataSourceError.NetworkFailure -> throw RepositoryError.NetworkError(e.message)
                is NetworkDataSourceError.InvalidData -> throw RepositoryError.InvalidInput(e.message)
            }
        }
    }

    override suspend fun updatePassword(email: String, currentPassword: String, newPassword: String) {
        Timber.d("Updating password for email: $email")
        throw RepositoryError.InvalidInput("Password update not supported")
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        Timber.d("Sending password reset email to: $email")
        throw RepositoryError.InvalidInput("Password reset email not supported")
    }

    override suspend fun resetPassword(email: String, newPassword: String) {
        Timber.d("Resetting password for email: $email")
        throw RepositoryError.InvalidInput("Password reset not supported")
    }

    override suspend fun loginUser(userId: String, password: String) {
        Timber.d("Logging in user with ID: $userId")
        throw RepositoryError.InvalidInput("Login not supported")
    }

    override suspend fun sendVerificationCode(email: String) {
        Timber.d("Sending verification code to: $email")
        throw RepositoryError.InvalidInput("Verification code not supported")
    }

    override suspend fun logoutUser() {
        Timber.d("Logging out user")
        throw RepositoryError.InvalidInput("Logout not supported")
    }

    override fun getCurrentUserId(): String? {
        Timber.d("Getting current user ID")
        return null // Not supported without AuthDataSource
    }
}