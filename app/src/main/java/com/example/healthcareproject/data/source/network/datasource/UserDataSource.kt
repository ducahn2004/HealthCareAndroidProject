package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    suspend fun saveUser(user: FirebaseUser)

    suspend fun loadUser(userId: String): FirebaseUser?

    suspend fun deleteUser(userId: String)

    suspend fun updateUser(userId: String, user: FirebaseUser)
}