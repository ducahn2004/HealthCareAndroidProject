package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.firebase.FirebaseService
import com.example.healthcareproject.data.source.network.model.FirebaseUser
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.healthcareproject.R
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class UserFirebaseDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseService: FirebaseService
) : UserDataSource {

    private val usersRef = firebaseService.getReference("users")
    private val verificationCodesRef = firebaseService.getReference("verificationCodes")


    // Utility function to escape email for Firebase paths
    private fun escapeEmail(email: String): String {
        return email.replace(".", "#")
    }


    override suspend fun saveUser(user: FirebaseUser) {
        try {
            val escapedUserId = escapeEmail(user.userId)
            println("Saving user with userId: ${user.userId}")
            println("Escaped userId: $escapedUserId")
            println("Saving user to path: users/$escapedUserId")
            usersRef.child(escapedUserId).setValue(user).await()
            Timber.tag("Firebase").d("Saved user: $user")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to write user: ${e.message}")
            throw Exception("Failed to write user: ${e.message}", e)
        }
    }

    override suspend fun loadUser(userId: String): FirebaseUser? {
        try {
            val escapedUserId = escapeEmail(userId)
            println("Loading user with userId: $userId")
            println("Escaped userId: $escapedUserId")
            println("Loading user from path: users/$escapedUserId")
            val snapshot = usersRef.child(escapedUserId).get().await()
            val user = snapshot.getValue(FirebaseUser::class.java)
            println("Loaded user: $user")
            return user
        } catch (e: Exception) {
            println("Error loading user: ${e.message}")
            throw e
        }
    }

    override suspend fun deleteUser(userId: String) {
        try {
            val escapedUserId = escapeEmail(userId)
            println("Deleting user with userId: $userId")
            println("Escaped userId: $escapedUserId")
            println("Deleting user at path: users/$escapedUserId")
            usersRef.child(escapedUserId).removeValue().await()
            Timber.tag("Firebase").d("Deleted user: $userId")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to delete user: ${e.message}")
            throw Exception("Failed to delete user: ${e.message}", e)
        }
    }

    override suspend fun updateUser(userId: String, user: FirebaseUser) {
        try {
            val escapedUserId = escapeEmail(userId)
            println("Updating user with userId: $userId")
            println("Escaped userId: $escapedUserId")
            println("Updating user at path: users/$escapedUserId")
            usersRef.child(escapedUserId).setValue(user).await()
            Timber.tag("Firebase").d("Updated user: $user")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to update user: ${e.message}")
            throw Exception("Failed to update user: ${e.message}", e)
        }
    }

    override suspend fun verifyCode(email: String, code: String) {
        try {
            val snapshot = verificationCodesRef
                .orderByChild("email")
                .equalTo(email)
                .get()
                .await()

            for (data in snapshot.children) {
                val storedCode = data.child("code").getValue(String::class.java)
                if (storedCode == code) {
                    data.ref.removeValue().await()
                    Timber.tag("Firebase").d("Verified code for email: $email")
                    return
                }
            }

            Timber.tag("Firebase").d("Invalid code for email: $email")
            throw Exception("Invalid verification code")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Verification failed for email: $email")
            throw Exception("Verification failed: ${e.message}", e)
        }
    }

    override suspend fun createUser(userId: String, password: String) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(userId, password).await()
            Timber.tag("Firebase").d("User created in Authentication: $userId")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "User creation failed: ${e.message}")
            throw Exception("User creation failed: ${e.message}", e)
        }
    }

    override suspend fun loginUser(userId: String, password: String) {
        try {
            firebaseAuth.signInWithEmailAndPassword(userId, password).await()
            Timber.tag("Firebase").d("User logged in: $userId")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Login failed: ${e.message}")
            throw Exception("Login failed: ${e.message}", e)
        }
    }

    override suspend fun googleSignIn(idToken: String) = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await()
        Timber.tag("Firebase").d("Google Sign-In successful")
    } catch (e: Exception) {
        Timber.tag("Firebase").e(e, "Google Sign-In failed: ${e.message}")
        throw Exception("Google Sign-In failed: ${e.message}", e)
    }

    override suspend fun updatePassword(newPassword: String) {
        try {
            val user = firebaseAuth.currentUser
                ?: throw Exception("No authenticated user found")
            user.updatePassword(newPassword).await()
            Timber.tag("Firebase").d("Password updated successfully")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to update password")
            throw Exception("Failed to update password: ${e.message}", e)
        }
    }

    override suspend fun updatePassword(userId: String, currentPassword: String, newPassword: String) {
        try {
            val user = firebaseAuth.currentUser
                ?: throw Exception("No authenticated user found")

            // Re-authenticate the user
            val credential = EmailAuthProvider.getCredential(userId, currentPassword)
            user.reauthenticate(credential).await()

            // Update the password
            user.updatePassword(newPassword).await()
            Timber.tag("Firebase").d("Password updated successfully for: $userId")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to update password for: $userId")
            throw Exception("Failed to update password: ${e.message}", e)
        }
    }
    override suspend fun sendPasswordResetEmail(email: String) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Timber.tag("Firebase").d("Password reset email sent to: $email")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to send password reset email to: $email")
            throw Exception("Failed to send password reset email: ${e.message}", e)
        }
    }
}