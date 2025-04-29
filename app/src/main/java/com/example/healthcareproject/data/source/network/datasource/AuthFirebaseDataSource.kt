package com.example.healthcareproject.data.source.network.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [AuthDataSource] using Firebase Authentication and Realtime Database.
 */
@Singleton
class AuthFirebaseDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) : AuthDataSource {

    private val codesRef = firebaseDatabase.getReference("verification_codes")
    private val codeExpirationMillis = TimeUnit.MINUTES.toMillis(5) // Mã hết hạn sau 5 phút

    override suspend fun loginUser(email: String, password: String): String {
        return try {
            withContext(Dispatchers.IO) {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid
                if (uid == null) {
                    Timber.tag("FirebaseAuth").e("Login failed: No UID returned for email $email")
                    throw Exception("Login failed: No user ID returned")
                }
                Timber.tag("FirebaseAuth").d("Logged in user with email $email, UID: $uid")
                uid
            }
        } catch (e: Exception) {
            Timber.tag("FirebaseAuth").e(e, "Failed to login user with email $email")
            throw Exception("Cannot login user with email $email: ${e.message}")
        }
    }

    override suspend fun registerUser(email: String, password: String): String {
        return try {
            withContext(Dispatchers.IO) {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid
                if (uid == null) {
                    Timber.tag("FirebaseAuth").e("Registration failed: No UID returned for email $email")
                    throw Exception("Registration failed: No user ID returned")
                }
                Timber.tag("FirebaseAuth").d("Registered user with email $email, UID: $uid")
                uid
            }
        } catch (e: Exception) {
            Timber.tag("FirebaseAuth").e(e, "Failed to register user with email $email")
            throw Exception("Cannot register user with email $email: ${e.message}")
        }
    }

    override suspend fun googleSignIn(idToken: String): String {
        return try {
            withContext(Dispatchers.IO) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = firebaseAuth.signInWithCredential(credential).await()
                val uid = result.user?.uid
                if (uid == null) {
                    Timber.tag("FirebaseAuth").e("Google sign-in failed: No UID returned")
                    throw Exception("Google sign-in failed: No user ID returned")
                }
                Timber.tag("FirebaseAuth").d("Signed in with Google, UID: $uid")
                uid
            }
        } catch (e: Exception) {
            Timber.tag("FirebaseAuth").e(e, "Failed to sign in with Google")
            throw Exception("Cannot sign in with Google: ${e.message}")
        }
    }

    override suspend fun sendVerificationCode(email: String) {
        try {
            withContext(Dispatchers.IO) {
                
                val methods = firebaseAuth.fetchSignInMethodsForEmail(email).await()
                if (methods.signInMethods.isNullOrEmpty()) {
                    Timber.tag("FirebaseAuth").e("No user found for email $email")
                    throw Exception("No user found for email $email")
                }
                // Tạo mã xác minh 6 chữ số
                val code = SecureRandom().nextInt(999999).toString().padStart(6, '0')
                // Lưu mã vào Realtime Database
                val codeData = mapOf(
                    "code" to code,
                    "timestamp" to System.currentTimeMillis()
                )
                codesRef.child(email.replace(".", ",")).setValue(codeData).await()
                Timber.tag("FirebaseAuth").d("Sent verification code $code to $email")
                // TODO: Gọi Firebase Cloud Function để gửi email chứa mã
            }
        } catch (e: Exception) {
            Timber.tag("FirebaseAuth").e(e, "Failed to send verification code to $email")
            throw Exception("Cannot send verification code to $email: ${e.message}")
        }
    }

    override suspend fun verifyCode(email: String, code: String) {
        try {
            withContext(Dispatchers.IO) {
                val snapshot = codesRef.child(email.replace(".", ",")).get().await()
                val codeData = snapshot.getValue(Map::class.java)
                if (codeData == null) {
                    Timber.tag("FirebaseAuth").e("No verification code found for $email")
                    throw Exception("No verification code found for $email")
                }
                val storedCode = codeData["code"] as? String
                val timestamp = (codeData["timestamp"] as? Long) ?: 0L
                if (System.currentTimeMillis() - timestamp > codeExpirationMillis) {
                    Timber.tag("FirebaseAuth").e("Verification code expired for $email")
                    throw Exception("Verification code expired for $email")
                }
                if (storedCode != code) {
                    Timber.tag("FirebaseAuth").e("Invalid verification code for $email")
                    throw Exception("Invalid verification code for $email")
                }
                // Xóa mã sau khi xác minh thành công
                codesRef.child(email.replace(".", ",")).removeValue().await()
                Timber.tag("FirebaseAuth").d("Verified code for $email")
            }
        } catch (e: Exception) {
            Timber.tag("FirebaseAuth").e(e, "Failed to verify code for $email")
            throw Exception("Cannot verify code for $email: ${e.message}")
        }
    }

    override suspend fun resetPassword(email: String, newPassword: String) {
        try {
            withContext(Dispatchers.IO) {

                val user = firebaseAuth.currentUser
                if (user == null || user.email != email) {

                    Timber.tag("FirebaseAuth").e("No user signed in or email mismatch for $email")
                    throw Exception("No user signed in or email mismatch for $email")
                }
                user.updatePassword(newPassword).await()
                Timber.tag("FirebaseAuth").d("Password reset successfully for $email")
            }
        } catch (e: Exception) {
            Timber.tag("FirebaseAuth").e(e, "Failed to reset password for $email")
            throw Exception("Cannot reset password for $email: ${e.message}")
        }
    }

    override suspend fun updatePassword(email: String, currentPassword: String, newPassword: String) {
        try {
            withContext(Dispatchers.IO) {
                val user = firebaseAuth.currentUser
                if (user == null || user.email != email) {
                    Timber.tag("FirebaseAuth").e("No user signed in or email mismatch for $email")
                    throw Exception("No user signed in or email mismatch")
                }

                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPassword)
                user.reauthenticate(credential).await()
                user.updatePassword(newPassword).await()
                Timber.tag("FirebaseAuth").d("Password updated successfully for $email")
            }
        } catch (e: Exception) {
            Timber.tag("FirebaseAuth").e(e, "Failed to update password for $email")
            throw Exception("Cannot update password for $email: ${e.message}")
        }
    }

    override suspend fun logout() {
        try {
            withContext(Dispatchers.IO) {
                firebaseAuth.signOut()
                Timber.tag("FirebaseAuth").d("Logged out user")
            }
        } catch (e: Exception) {
            Timber.tag("FirebaseAuth").e(e, "Failed to logout user")
            throw Exception("Cannot logout user: ${e.message}")
        }
    }

    override fun getCurrentUserId(): String? {
        val uid = firebaseAuth.currentUser?.uid
        Timber.tag("FirebaseAuth").d("Current user ID: $uid")
        return uid
    }
}