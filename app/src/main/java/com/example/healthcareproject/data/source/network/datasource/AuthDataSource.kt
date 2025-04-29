package com.example.healthcareproject.data.source.network.datasource

/**
 * Interface defining authentication operations.
 */
interface AuthDataSource {

    /**
     * Logs in a user with email and password.
     * @param email The user's email.
     * @param password The user's password.
     * @return The UID of the authenticated user.
     * @throws Exception if the operation fails (e.g., invalid credentials, network error).
     */
    suspend fun loginUser(email: String, password: String): String

    /**
     * Registers a new user with email and password.
     * @param email The user's email.
     * @param password The user's password.
     * @return The UID of the newly created user.
     * @throws Exception if the operation fails (e.g., email already exists, weak password).
     */
    suspend fun registerUser(email: String, password: String): String

    /**
     * Signs in a user with a Google ID token.
     * @param idToken The Google ID token.
     * @return The UID of the authenticated user.
     * @throws Exception if the operation fails (e.g., invalid token, network error).
     */
    suspend fun googleSignIn(idToken: String): String

    /**
     * Sends a 6-digit verification code to the user's email for password reset.
     * @param email The user's email.
     * @throws Exception if the operation fails (e.g., user not found, network error).
     */
    suspend fun sendVerificationCode(email: String)

    /**
     * Verifies the 6-digit code sent to the user's email.
     * @param email The user's email.
     * @param code The verification code.
     * @throws Exception if the operation fails (e.g., invalid code, code expired).
     */
    suspend fun verifyCode(email: String, code: String)

    /**
     * Resets the user's password after successful code verification.
     * @param email The user's email.
     * @param newPassword The new password.
     * @throws Exception if the operation fails (e.g., user not found, network error).
     */
    suspend fun resetPassword(email: String, newPassword: String)

    /**
     * Updates the user's password after re-authentication.
     * @param email The user's email.
     * @param currentPassword The current password.
     * @param newPassword The new password.
     * @throws Exception if the operation fails (e.g., incorrect current password, weak new password).
     */
    suspend fun updatePassword(email: String, currentPassword: String, newPassword: String)

    /**
     * Logs out the current user.
     * @throws Exception if the operation fails (e.g., no user signed in).
     */
    suspend fun logout()

    /**
     * Gets the UID of the currently signed-in user.
     * @return The UID of the current user, or null if no user is signed in.
     */
    fun getCurrentUserId(): String?
}