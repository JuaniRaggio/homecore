package com.itba.homecore.data.repository

import com.itba.homecore.data.model.User

/**
 * Authentication contract. The mock implementation ([MockAuthRepository]) accepts any
 * credentials to test the UI without a backend; the real one ([RemoteAuthRepository])
 * uses the HCI API plus DataStore session persistence.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, lastName: String, email: String, password: String): Result<Unit>
    suspend fun logout()
    suspend fun restoreSession(): Boolean

    /** Confirms the account with the code emailed on registration (RF2). */
    suspend fun verifyAccount(code: String): Result<Unit>

    /** Resends the verification code. Register already sends it: this is ONLY for resending. */
    suspend fun sendVerification(email: String): Result<Unit>

    /** Requests a password-recovery code by email (RF3). */
    suspend fun forgotPassword(email: String): Result<Unit>

    /** Sets a new password using the recovery code (RF3). */
    suspend fun resetPassword(code: String, newPassword: String): Result<Unit>

    /** Changes the password of the logged-in user (RF4). */
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>

    /** Profile of the logged-in user. */
    suspend fun getProfile(): Result<User>
}
