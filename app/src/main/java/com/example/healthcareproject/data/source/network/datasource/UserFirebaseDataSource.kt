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

    override suspend fun getUidByEmail(email: String): String? {
        try {
            val escapedEmail = escapeEmail(email)
            println("Looking up UID for email: $email")
            println("Escaped email: $escapedEmail")
            println("Querying path: emailToUid/$escapedEmail")
            val snapshot = emailToUidRef.child(escapedEmail).get().await()
            val uid = snapshot.getValue(String::class.java)
            println("Found UID: $uid for email: $email")
            return uid
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to get UID for email: $email")
            return null
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
        try {
            println("Loading user with UID: $uid")
            println("Loading user from path: users/$uid")
            val snapshot = usersRef.child(uid).get().await()
            val user = snapshot.getValue(FirebaseUser::class.java)
            println("Loaded user: $user")
            return user
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

    override suspend fun createUser(email: String, password: String) {
        try {
            println("Creating user in Authentication with email: $email")
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Timber.tag("Firebase").d("User created in Authentication: $email")
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
            // Since the user has already verified their identity via a code,
            // we need to sign them in temporarily to update the password.
            // However, Firebase client SDK requires the user to be signed in.
            // For a proper reset, we should use a password reset link, but since you're using a verification code,
            // we'll assume the user has been authenticated or use a custom flow.

            // First, sign the user out to ensure a clean state
            firebaseAuth.signOut()

            // Sign in the user temporarily (assuming the verification code flow has confirmed their identity)
            // In a real app, you might use a custom token or a password reset link instead.
            // For this example, we'll assume the verification code flow has already validated the user.
            // We need to fetch the user by email and update their password.

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
}