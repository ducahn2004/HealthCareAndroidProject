package com.example.healthcareproject.data.source.network.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class AuthFirebaseDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthDataSource {

    override suspend fun loginUser(email: String, password: String) {
        Timber.d("Attempting to log in user with email: $email")
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Timber.d("User logged in successfully: $email")
        } catch (e: Exception) {
            Timber.e(e, "Login failed for email: $email")
            throw Exception("Login failed: ${e.message}", e)
        }
    }

    override suspend fun logout() {
        Timber.d("Logging out current user")
        firebaseAuth.signOut()
        Timber.d("User logged out successfully")
    }

    override suspend fun registerUser(email: String, password: String) {
        Timber.d("Attempting to register user with email: $email")
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Timber.d("User registered successfully: $email")
        } catch (e: Exception) {
            Timber.e(e, "Registration failed for email: $email")
            throw Exception("Registration failed: ${e.message}", e)
        }
    }

    override suspend fun googleSignIn(idToken: String) {
        Timber.d("Attempting Google Sign-In")
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            Timber.d("Google Sign-In successful")
        } catch (e: Exception) {
            Timber.e(e, "Google Sign-In failed")
            throw Exception("Google Sign-In failed: ${e.message}", e)
        }
    }

    override suspend fun resetPassword(email: String) {
        Timber.d("Attempting to reset password for email: $email")
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Timber.d("Password reset email sent successfully to: $email")
        } catch (e: Exception) {
            Timber.e(e, "Password reset failed for email: $email")
            throw Exception("Password reset failed: ${e.message}", e)
        }
    }

    override suspend fun sendVerificationCode(email: String) {
        Timber.d("Attempting to send verification code to email: $email")
        try {
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
            Timber.d("Verification code sent successfully to: $email")
        } catch (e: Exception) {
            Timber.e(e, "Sending verification code failed for email: $email")
            throw Exception("Sending verification code failed: ${e.message}", e)
        }
    }

    override suspend fun updatePassword(
        email: String,
        currentPassword: String,
        newPassword: String
    ) {
        Timber.d("Attempting to update password for email: $email")
        try {
            loginUser(email, currentPassword)
            firebaseAuth.currentUser?.updatePassword(newPassword)?.await()
            Timber.d("Password updated successfully for email: $email")
        } catch (e: Exception) {
            Timber.e(e, "Password update failed for email: $email")
            throw Exception("Password update failed: ${e.message}", e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        Timber.d("Attempting to send password reset email to: $email")
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Timber.d("Password reset email sent successfully to: $email")
        } catch (e: Exception) {
            Timber.e(e, "Sending password reset email failed for email: $email")
            throw Exception("Sending password reset email failed: ${e.message}", e)
        }
    }

    override fun getCurrentUserId(): String? {
        val userId = firebaseAuth.currentUser?.uid
        Timber.d("Current user ID: $userId")
        return userId
    }
}