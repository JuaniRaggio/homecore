package com.itba.homecore.data.repository

import com.itba.homecore.data.model.User

/** The email already has an account: the flow should send the user to sign in. */
class EmailAlreadyRegisteredException(message: String) : Exception(message)

/**
 * Authentication contract. Implemented by [RemoteAuthRepository], which uses the HCI API
 * plus DataStore session persistence.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, lastName: String, email: String, password: String): Result<Unit>
    suspend fun logout()
    suspend fun restoreSession(): Boolean

    /** Confirms the account with the code emailed on registration. */
    suspend fun verifyAccount(code: String): Result<Unit>

    /** Resends the verification code to the account email. */
    suspend fun sendVerification(email: String): Result<Unit>

    /** Requests a password-recovery code by email. */
    suspend fun forgotPassword(email: String): Result<Unit>

    /** Sets a new password using the recovery code. */
    suspend fun resetPassword(code: String, newPassword: String): Result<Unit>

    /** Changes the password of the logged-in user. */
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>

    /** Profile of the logged-in user. */
    suspend fun getProfile(): Result<User>

    /** Locally persisted user (name + email saved at login), or null if there is no session. */
    suspend fun getSessionUser(): User?
}
