package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    suspend fun saveUser(uid: String, user: FirebaseUser)

    suspend fun loadUser(uid: String): FirebaseUser?

    suspend fun deleteUser(uid: String)

    suspend fun updateUser(uid: String, user: FirebaseUser)

    suspend fun getUidByEmail(email: String): String?

    suspend fun getEmailByUid(uid: String): String?

}
