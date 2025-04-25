package com.example.healthcareproject.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    suspend operator fun invoke(idToken: String) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            // Optionally save user data to your backend or database
        } catch (e: Exception) {
            throw Exception("Google Sign-In failed: ${e.message}")
        }
    }
}