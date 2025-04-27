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
            // Create user in Firebase Authentication and get UID
            val uid = networkDataSource.createUser(userId, password)
            println("User created in Authentication: $userId with UID: $uid")

            // Parse gender and blood type from strings to enums
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

            // Create domain User object
            val user = User(
                userId = userId,
                password = password,
                name = name,
                address = address,
                dateOfBirth = java.time.LocalDate.parse(dateOfBirth, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                gender = genderEnum,
                bloodType = bloodTypeEnum,
                phone = phone,
                createdAt = java.time.LocalDateTime.now(),
                updatedAt = java.time.LocalDateTime.now()
            )

            // Save to network (Firebase Realtime Database)
            networkDataSource.saveUser(user.toNetwork(), uid)

            // Save to local (Room database)
            localDataSource.upsert(user.toLocal())

            // Send verification code (default code "000000" will be used)
            networkDataSource.sendVerificationCode(userId)

            uid // Return the UID as required by the interface
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

        withContext(dispatcher) {
            // Fetch the UID using the userId (email)
            val uid = networkDataSource.getUidByEmail(userId)
                ?: throw Exception("Failed to retrieve UID for user $userId")

            // Update user in both local and network data sources
            localDataSource.upsert(user.toLocal())
            networkDataSource.updateUser(uid, user.toNetwork())
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
            // Fetch the UID to delete from the network
            val uid = networkDataSource.getUidByEmail(userId)
                ?: throw Exception("Failed to retrieve UID for user $userId")
            localDataSource.deleteById(userId)
            networkDataSource.deleteUser(uid)
        }
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