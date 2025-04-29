package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class UserFirebaseDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : UserDataSource {

    private val usersRef = firebaseDatabase.getReference("users")

    override suspend fun saveUser(user: FirebaseUser) {
        try {
            usersRef.child(user.userId).setValue(user).await()
            Timber.tag("Firebase").d("Saved user: $user")
        } catch (e: Exception) {
            throw Exception("Failed to write user: ${e.message}", e)
        }
    }

    override suspend fun loadUser(userId: String): FirebaseUser? {
        val snapshot = usersRef.child(userId).get().await()
        return snapshot.getValue(FirebaseUser::class.java)
    }

    override suspend fun deleteUser(userId: String) {
        usersRef.child(userId).removeValue().await()
    }

    override suspend fun updateUser(userId: String, user: FirebaseUser) {
        usersRef.child(userId).setValue(user).await()
    }
}