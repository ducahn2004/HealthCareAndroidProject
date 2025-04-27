package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.dao.UserDao
import com.example.healthcareproject.data.source.network.datasource.UserDataSource
import com.example.healthcareproject.di.DefaultDispatcher
import com.example.healthcareproject.domain.model.BloodType
import com.example.healthcareproject.domain.model.Gender
import com.example.healthcareproject.domain.model.User
import com.example.healthcareproject.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

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
    ): String {
        return withContext(dispatcher) {
            val uid = networkDataSource.createUser(userId, password)
            println("User created in Authentication: $userId with UID: $uid")

            val genderEnum = try {
                Gender.valueOf(gender.replace(" ", "").replaceFirstChar { it.uppercase() })
            } catch (e: IllegalArgumentException) {
                throw Exception("Invalid gender value: $gender")
            }

            val bloodTypeEnum = try {
                BloodType.valueOf(bloodType.replace(" ", "").replaceFirstChar { it.uppercase() })
            } catch (e: IllegalArgumentException) {
                throw Exception("Invalid blood type value: $bloodType")
            }

            if (!isValidDateFormat(dateOfBirth) || !isValidDate(dateOfBirth)) {
                throw Exception("Invalid date of birth. Use dd/MM/yyyy and ensure it's a valid date.")
            }

            val user = User(
                userId = userId,
                password = password,
                name = name,
                address = address,
                dateOfBirth = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.US)),
                gender = genderEnum,
                bloodType = bloodTypeEnum,
                phone = phone,
                createdAt = java.time.LocalDateTime.now(),
                updatedAt = java.time.LocalDateTime.now()
            )

            networkDataSource.saveUser(user.toNetwork(), uid)
            localDataSource.upsert(user.toLocal())
            networkDataSource.sendVerificationCode(userId)
            uid
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
    ) {
        withContext(dispatcher) {
            if (!isValidDateFormat(dateOfBirth) || !isValidDate(dateOfBirth)) {
                throw Exception("Invalid date of birth. Use dd/MM/yyyy and ensure it's a valid date.")
            }

            try {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.US)
                val localDate = LocalDate.parse(dateOfBirth, formatter)

                val user = getUser(userId)?.copy(
                    password = password,
                    name = name,
                    address = address,
                    dateOfBirth = localDate,
                    gender = Gender.valueOf(gender.replace(" ", "").replaceFirstChar { it.uppercase() }),
                    bloodType = BloodType.valueOf(bloodType.replace(" ", "").replaceFirstChar { it.uppercase() }),
                    phone = phone,
                    updatedAt = java.time.LocalDateTime.now()
                ) ?: throw Exception("User (id $userId) not found")

                val uid = networkDataSource.getUidByEmail(userId)
                    ?: throw Exception("Failed to retrieve UID for user $userId")

                localDataSource.upsert(user.toLocal())
                networkDataSource.updateUser(uid, user.toNetwork())
            } catch (e: Exception) {
                throw Exception("Failed to update user: ${e.message}")
            }
        }
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

    private fun isValidDateFormat(date: String): Boolean {
        return date.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            sdf.isLenient = false
            val parsedDate = sdf.parse(date)
            parsedDate != null && parsedDate.before(Date())
        } catch (e: Exception) {
            false
        }
    }
}