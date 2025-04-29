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
    ) {
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
        networkDataSource.saveUser(user.toNetwork())
        localDataSource.upsert(user.toLocal())
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

        localDataSource.upsert(user.toLocal())
        networkDataSource.updateUser(userId, user.toNetwork())
    }

    override suspend fun refreshUser(userId: String) {
        refresh(userId)
    }

    override fun getUserStream(userId: String): Flow<User?> {
        return localDataSource.observeById(userId)
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun getUser(userId: String, forceUpdate: Boolean): User? {
        if (forceUpdate) {
            refresh(userId)
        }
        return localDataSource.getById(userId)?.toExternal()
    }

    override suspend fun deleteUser(userId: String) {
        localDataSource.deleteById(userId)
        networkDataSource.deleteUser(userId)
    }

    override suspend fun loginUser(email: String, password: String) {
        authDataSource.loginUser(email, password)
    }

    override suspend fun logoutUser() {
        authDataSource.logout()
    }

    override suspend fun resetPassword(email: String) {
        authDataSource.resetPassword(email)
    }

    override fun getCurrentUserId(): String? {
        return authDataSource.getCurrentUserId()
    }

    override suspend fun sendVerificationEmail(email: String) {
        authDataSource.sendVerificationCode(email)
    }

    override suspend fun refresh(userId: String) {
        val firebaseUser = networkDataSource.loadUser(userId)
        if (firebaseUser != null && firebaseUser.userId.isNotEmpty() && firebaseUser.name.isNotEmpty()) {
            localDataSource.upsert(firebaseUser.toLocal())
        } else {
            // Log or handle invalid data case
            throw IllegalArgumentException("Invalid user data received from network")
        }
    }
}