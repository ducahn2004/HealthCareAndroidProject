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
        userId: String,
        password: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ): String {
        val generatedUserId = withContext(dispatcher) {
            userId.ifEmpty { java.util.UUID.randomUUID().toString() }
        }
        val user = User(
            userId = generatedUserId,
            password = password,
            name = name,
            address = address,
            dateOfBirth = java.time.LocalDate.parse(dateOfBirth),
            gender = com.example.healthcareproject.domain.model.Gender.valueOf(gender),
            bloodType = com.example.healthcareproject.domain.model.BloodType.valueOf(bloodType),
            phone = phone,
            createdAt = java.time.LocalDateTime.now(),
            updatedAt = java.time.LocalDateTime.now()
        )

        // Đăng ký người dùng với auth service trước để lấy UID
        val uid = authDataSource.registerUser(generatedUserId, password)

        // Sau đó lưu thông tin chi tiết với UID đã nhận
        networkDataSource.saveUser(uid.toString(), user.toNetwork())
        localDataSource.upsert(user.toLocal())

        return generatedUserId
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
    ) {
        val user = getUser(userId)?.copy(
            password = password,
            name = name,
            address = address,
            dateOfBirth = java.time.LocalDate.parse(dateOfBirth),
            gender = com.example.healthcareproject.domain.model.Gender.valueOf(gender),
            bloodType = com.example.healthcareproject.domain.model.BloodType.valueOf(bloodType),
            phone = phone,
            updatedAt = java.time.LocalDateTime.now()
        ) ?: throw Exception("User (id $userId) not found")

        val uid = networkDataSource.getUidByEmail(userId)
            ?: throw Exception("Failed to retrieve UID for user $userId")

        localDataSource.upsert(user.toLocal())
        networkDataSource.updateUser(uid, user.toNetwork())
    }

    override suspend fun refreshUser(userId: String) {
        refresh(userId)
    }

    override fun getUserStream(userId: String): Flow<User?> {
        return localDataSource.observeById(userId)
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun verifyCode(email: String, code: String) {
        withContext(dispatcher) {
            try {
                networkDataSource.verifyCode(email, code)
            } catch (e: Exception) {
                throw Exception("Verification failed: ${e.message}")
            }
        }
    }

    override suspend fun getUser(userId: String, forceUpdate: Boolean): User? {
        if (forceUpdate) {
            refresh(userId)
        }
        return localDataSource.getById(userId)?.toExternal()
    }

    override suspend fun getUserByUid(uid: String, forceUpdate: Boolean): User? {
        if (forceUpdate) {
            withContext(dispatcher) {
                val firebaseUser = networkDataSource.loadUser(uid)
                if (firebaseUser != null && firebaseUser.userId.isNotEmpty() && firebaseUser.name.isNotEmpty()) {
                    localDataSource.upsert(firebaseUser.toLocal())
                } else {
                    throw IllegalArgumentException("Invalid user data received from network")
                }
            }
        }
        val userId = networkDataSource.getEmailByUid(uid)
            ?: throw Exception("Failed to retrieve email for UID $uid")
        return localDataSource.getById(userId)?.toExternal()
    }

    override suspend fun deleteUser(userId: String) {
        withContext(dispatcher) {
            val uid = networkDataSource.getUidByEmail(userId)
                ?: throw Exception("Failed to retrieve UID for user $userId")
            localDataSource.deleteById(userId)
            networkDataSource.deleteUser(uid)
        }
    }

    override suspend fun updatePassword(
        email: String,
        currentPassword: String,
        newPassword: String
    ) {
        networkDataSource.updatePassword(email, currentPassword, newPassword)
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        networkDataSource.sendPasswordResetEmail(email)
    }

    override suspend fun resetPassword(email: String, newPassword: String) {
        networkDataSource.resetPassword(email, newPassword)
    }

    override suspend fun loginUser(userId: String, password: String) {
        networkDataSource.loginUser(userId, password)
    }

    override suspend fun sendVerificationCode(email: String) {
        networkDataSource.sendVerificationCode(email)
    }

    override suspend fun logoutUser() {
        authDataSource.logout()
    }

    override fun getCurrentUserId(): String? {
        return authDataSource.getCurrentUserId()
    }

    override suspend fun refresh(userId: String) {
        withContext(dispatcher) {
            val uid = networkDataSource.getUidByEmail(userId)
                ?: throw Exception("Failed to retrieve UID for user $userId")
            val firebaseUser = networkDataSource.loadUser(uid)
            if (firebaseUser != null && firebaseUser.userId.isNotEmpty() && firebaseUser.name.isNotEmpty()) {
                localDataSource.upsert(firebaseUser.toLocal())
            } else {
                throw IllegalArgumentException("Invalid user data received from network")
            }
        }
    }
}