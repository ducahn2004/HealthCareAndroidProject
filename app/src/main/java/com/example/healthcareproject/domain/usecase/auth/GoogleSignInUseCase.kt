package com.example.healthcareproject.domain.usecase.auth

import com.example.healthcareproject.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(idToken: String): Result<FirebaseUser?> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user
            userRepository.refresh()
            // Log analytics event (optional)
            Result.success(user)
        } catch (e: FirebaseAuthException) {
            when (e.errorCode) {
                "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                    Result.failure(AccountExistsException(e))
                }
                else -> Result.failure(Exception("Google Sign-In failed: ${e.message}", e))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Google Sign-In failed: ${e.message}", e))
        }
    }
}

// Custom exception for account linking scenarios
class AccountExistsException(cause: Throwable) : Exception("Account exists with different credential", cause)