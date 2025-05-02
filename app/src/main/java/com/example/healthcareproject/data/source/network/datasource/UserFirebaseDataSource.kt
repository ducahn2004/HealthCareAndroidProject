package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class UserFirebaseDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : UserDataSource {

    private val usersRef = firebaseDatabase.getReference("users")

    override suspend fun saveUser(user: FirebaseUser) {
        try {
            withContext(Dispatchers.IO) {
                usersRef.child(user.userId).setValue(user).await()
                Timber.tag("Firebase").d("Saved user with UID ${user.userId}: $user")
            }
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to save user with UID ${user.userId}")
            throw Exception("Cannot save user with UID ${user.userId}: ${e.message}")
        }
    }


    override suspend fun loadUser(uid: String): FirebaseUser? {
        return try {
            withContext(Dispatchers.IO) {
                val snapshot = usersRef.child(uid).get().await()
                snapshot.getValue(FirebaseUser::class.java).also { user ->
                    if (user == null) {
                        Timber.tag("Firebase").w("User with UID $uid not found")
                    } else {
                        Timber.tag("Firebase").d("Loaded user with UID $uid: $user")
                    }
                }
            }
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to load user with UID $uid")
            throw Exception("Cannot load user with UID $uid: ${e.message}")
        }
    }

    override suspend fun updateUser(uid: String, user: FirebaseUser) {
        try {
            withContext(Dispatchers.IO) {
                usersRef.child(uid).setValue(user).await()
                Timber.tag("Firebase").d("Updated user with UID $uid: $user")
            }
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to update user with UID $uid")
            throw Exception("Cannot update user with UID $uid: ${e.message}")
        }
    }

    override suspend fun deleteUser(uid: String) {
        try {
            withContext(Dispatchers.IO) {
                usersRef.child(uid).removeValue().await()
                Timber.tag("Firebase").d("Deleted user with UID $uid")
            }
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to delete user with UID $uid")
            throw Exception("Cannot delete user with UID $uid: ${e.message}")
        }
    }

    override suspend fun getUidByEmail(email: String): String? {
        return try {
            withContext(Dispatchers.IO) {
                val snapshot = usersRef.orderByChild("userId").equalTo(email).get().await()
                val uid = snapshot.children.firstOrNull()?.key
                if (uid == null) {
                    Timber.tag("Firebase").w("UID not found for email $email")
                } else {
                    Timber.tag("Firebase").d("Found UID $uid for email $email")
                }
                uid
            }
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to get UID for email $email")
            throw Exception("Cannot get UID for email $email: ${e.message}")
        }
    }

    override suspend fun getEmailByUid(uid: String): String? {
        return try {
            withContext(Dispatchers.IO) {
                val snapshot = usersRef.child(uid).get().await()
                val user = snapshot.getValue(FirebaseUser::class.java)
                val email = user?.userId
                if (email == null) {
                    Timber.tag("Firebase").w("Email not found for UID $uid")
                } else {
                    Timber.tag("Firebase").d("Found email $email for UID $uid")
                }
                email
            }
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to get email for UID $uid")
            throw Exception("Cannot get email for UID $uid: ${e.message}")
        }
    }
}