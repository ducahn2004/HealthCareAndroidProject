package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.firebase.FirebaseService
import com.example.healthcareproject.data.source.network.model.FirebaseUser
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.healthcareproject.R
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random


class UserFirebaseDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseService: FirebaseService,
    private val firebaseFunctions: FirebaseFunctions
) : UserDataSource {

    private val usersRef = firebaseService.getReference("users")
    private val emailToUidRef = firebaseService.getReference("emailToUid")
    private val verificationCodesRef = firebaseService.getReference("verificationCodes")

    // Utility function to escape email for Firebase paths
    private fun escapeEmail(email: String): String {
        return email.replace(".", "#")
    }

    override suspend fun getUidByEmail(email: String): String? {
        return try {
            val escapedEmail = escapeEmail(email)
            println("Looking up UID for email: $email")
            println("Escaped email: $escapedEmail")
            println("Querying path: emailToUid/$escapedEmail")
            val snapshot = emailToUidRef.child(escapedEmail).get().await()
            val uid = snapshot.getValue(String::class.java)
            println("Found UID: $uid for email: $email")
            uid
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to get UID for email: $email")
            null
        }
    }

    override suspend fun getEmailByUid(uid: String): String? {
        return try {
            println("Looking up email for UID: $uid")
            val snapshot = emailToUidRef.orderByValue().equalTo(uid).get().await()
            for (child in snapshot.children) {
                val escapedEmail = child.key ?: continue
                val email = escapedEmail.replace("#", ".")
                println("Found email: $email for UID: $uid")
                return email
            }
            println("No email found for UID: $uid")
            null
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to get email for UID: $uid")
            null
        }
    }


    override suspend fun saveUser(user: FirebaseUser, uid: String) {
        try {
            println("Saving user with userId: ${user.userId}, UID: $uid")
            println("Saving user to path: users/$uid")
            // Save user data under UID
            val userData = mapOf(
                "name" to user.name,
                "address" to user.address,
                "dateOfBirth" to user.dateOfBirth,
                "gender" to user.gender.toString(),
                "bloodType" to user.bloodType.toString(),
                "phone" to user.phone,
                "userId" to user.userId
            )
            usersRef.child(uid).setValue(userData).await()

            // Save email-to-UID mapping
            val escapedEmail = escapeEmail(user.userId)
            println("Saving email-to-UID mapping: emailToUid/$escapedEmail -> $uid")
            emailToUidRef.child(escapedEmail).setValue(uid).await()

            Timber.tag("Firebase").d("Saved user with UID: $uid")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to write user with UID $uid: ${e.message}")
            throw Exception("Failed to write user with UID $uid: ${e.message}", e)
        }
    }

    override suspend fun loadUser(uid: String): FirebaseUser? {
        return try {
            println("Loading user with UID: $uid")
            println("Loading user from path: users/$uid")
            val snapshot = usersRef.child(uid).get().await()
            val user = snapshot.getValue(FirebaseUser::class.java)
            println("Loaded user: $user")
            user
        } catch (e: Exception) {
            println("Error loading user with UID $uid: ${e.message}")
            throw e
        }
    }

    override suspend fun deleteUser(uid: String) {
        try {
            println("Deleting user with UID: $uid")
            println("Deleting user at path: users/$uid")
            // Delete user data
            usersRef.child(uid).removeValue().await()

            // Delete email-to-UID mapping
            val user = loadUser(uid)
            if (user != null) {
                val escapedEmail = escapeEmail(user.userId)
                println("Deleting email-to-UID mapping at: emailToUid/$escapedEmail")
                emailToUidRef.child(escapedEmail).removeValue().await()
            }

            Timber.tag("Firebase").d("Deleted user with UID: $uid")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to delete user with UID $uid: ${e.message}")
            throw Exception("Failed to delete user with UID $uid: ${e.message}", e)
        }
    }

    override suspend fun updateUser(uid: String, user: FirebaseUser) {
        try {
            println("Updating user with UID: $uid")
            println("Updating user at path: users/$uid")
            val userData = mapOf(
                "name" to user.name,
                "address" to user.address,
                "dateOfBirth" to user.dateOfBirth,
                "gender" to user.gender.toString(),
                "bloodType" to user.bloodType.toString(),
                "phone" to user.phone,
                "userId" to user.userId
            )
            usersRef.child(uid).setValue(userData).await()
            Timber.tag("Firebase").d("Updated user with UID: $uid")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to update user with UID $uid: ${e.message}")
            throw Exception("Failed to update user with UID $uid: ${e.message}", e)
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

    override suspend fun createUser(email: String, password: String): String {
        try {
            println("Creating user in Authentication with email: $email")
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Failed to get UID after user creation")
            Timber.tag("Firebase").d("User created in Authentication: $email with UID: $uid")
            return uid
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "User creation failed: ${e.message}")
            throw Exception("User creation failed: ${e.message}", e)
        }
    }

    override suspend fun loginUser(email: String, password: String) {
        try {
            println("Logging in user with email: $email")
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Timber.tag("Firebase").d("User logged in: $email")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Login failed: ${e.message}")
            throw Exception("Login failed: ${e.message}", e)
        }
    }

    override suspend fun googleSignIn(idToken: String) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            Timber.tag("Firebase").d("Google Sign-In successful")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Google Sign-In failed: ${e.message}")
            throw Exception("Google Sign-In failed: ${e.message}", e)
        }
    }

    override suspend fun updatePassword(email: String, currentPassword: String, newPassword: String) {
        try {
            val user = firebaseAuth.currentUser
                ?: throw Exception("No authenticated user found")

            // Re-authenticate the user
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()

            // Update the password
            user.updatePassword(newPassword).await()
            Timber.tag("Firebase").d("Password updated successfully for: $email")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to update password for: $email")
            throw Exception("Failed to update password: ${e.message}", e)
        }
    }

    override suspend fun resetPassword(email: String, newPassword: String) {
        try {

            firebaseAuth.signOut()

            val user = firebaseAuth.currentUser
            if (user != null && user.email == email) {
                user.updatePassword(newPassword).await()
                Timber.tag("Firebase").d("Password reset successfully for: $email")
            } else {
                throw Exception("User must be signed in to reset password. Please complete verification.")
            }
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to reset password for: $email")
            throw Exception("Failed to reset password: ${e.message}", e)
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
    override suspend fun sendVerificationCode(email: String) {
        try {
            // Sử dụng mã cố định "000000" thay vì tạo mã ngẫu nhiên
            val code = "000000"
            // Lưu mã vào database
            val codeData = mapOf(
                "email" to email,
                "code" to code
            )
            verificationCodesRef.push().setValue(codeData).await()
            // Bỏ gọi Cloud Function để gửi email
            // firebaseFunctions.getHttpsCallable("sendVerificationCode")
            //     .call(data)
            //     .await()
            Timber.tag("Firebase").d("Verification code set to 000000 for email: $email")
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to set verification code for: $email")
            throw Exception("Failed to set verification code: ${e.message}", e)
        }
    }
}