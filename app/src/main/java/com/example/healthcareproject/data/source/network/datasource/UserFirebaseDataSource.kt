package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.firebase.FirebaseService
import com.example.healthcareproject.data.source.network.model.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserFirebaseDataSource : UserDataSource {

    private val usersRef = FirebaseService.getReference("users")

    override suspend fun writeUser(user: FirebaseUser) {
        usersRef.child(user.userId).setValue(user).await()
    }

    override suspend fun readUser(userId: String): FirebaseUser? {
        val snapshot = usersRef.child(userId).get().await()
        return snapshot.getValue(FirebaseUser::class.java)
    }

    override suspend fun deleteUser(userId: String) {
        usersRef.child(userId).removeValue().await()
    }

    override suspend fun updateUser(userId: String, user: FirebaseUser) {
        usersRef.child(userId).setValue(user).await()
    }

    override fun getUserRealtime(userId: String): Flow<FirebaseUser> = callbackFlow {
        val ref = usersRef.child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(FirebaseUser::class.java)
                if (user != null) {
                    trySend(user).isSuccess
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}