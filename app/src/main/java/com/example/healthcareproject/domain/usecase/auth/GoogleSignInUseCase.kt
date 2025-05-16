package com.example.healthcareproject.domain.usecase.auth

import com.example.healthcareproject.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(idToken: String): Result<FirebaseUser?> {
        Timber.d("Executing GoogleSignInUseCase with ID token")
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user
            userRepository.refresh()
            Timber.d("Google Sign-In successful, UID: ${user?.uid}")
            Result.success(user)
        } catch (e: FirebaseAuthException) {
            when (e.errorCode) {
                "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                    Timber.e(e, "Google Sign-In failed: Account exists with different credential")
                    Result.failure(AccountExistsException(e))
                }
                "ERROR_INVALID_USER" -> {
                    Timber.e(e, "Google Sign-In blocked: Account not registered")
                    Result.failure(FirebaseAuthInvalidUserException("ERROR_INVALID_USER", "Account not registered"))
                }
                else -> {
                    Timber.e(e, "Google Sign-In failed: ${e.message}")
                    Result.failure(Exception("Google Sign-In failed: ${e.message}", e))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Google Sign-In failed: Unknown error")
            Result.failure(Exception("Google Sign-In failed: ${e.message}", e))
        }
    }
}

// Custom exception for account linking scenarios
class AccountExistsException(cause: Throwable) : Exception("Account exists with different credential", cause)