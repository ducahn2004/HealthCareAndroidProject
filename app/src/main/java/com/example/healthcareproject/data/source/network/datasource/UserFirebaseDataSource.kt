package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.firebase.FirebaseService
import com.example.healthcareproject.data.source.network.model.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class UserFirebaseDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseService: FirebaseService
) : UserDataSource {

    private val usersRef = firebaseService.getReference("users")
    private val verificationCodesRef = firebaseService.getReference("verificationCodes")

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
        } catch (e: Exception) {
            throw Exception("User creation failed: ${e.message}", e)
        }
    }

    override suspend fun loginUser(userId: String, password: String) {
        try {
            firebaseAuth.signInWithEmailAndPassword(userId, password).await()
        } catch (e: Exception) {
            throw Exception("Login failed: ${e.message}", e)
        }
    }

    override suspend fun googleSignIn(idToken: String) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
        } catch (e: Exception) {
            throw Exception("Google Sign-In failed: ${e.message}", e)
        }
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