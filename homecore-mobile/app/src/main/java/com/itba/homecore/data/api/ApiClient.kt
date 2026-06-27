package com.itba.homecore.data.api

/**
 * In-memory holder for the session JWT, shared between the network layer ([KtorClient]
 * reads it to add the `Authorization` header) and the auth/session code (which sets and
 * clears it). The token is persisted separately in DataStore via SessionManager.
 */
object ApiClient {
    @Volatile
    private var token: String? = null

    fun setToken(t: String?) { this.token = t }

    /** True while an auth token is held in memory. The 401 handler clears it. */
    fun hasToken(): Boolean = token != null

    /** Current JWT (or null). Read by [KtorClient] to add the Bearer header. */
    fun currentToken(): String? = token

    /** Clears the in-memory token (used by the 401 handler). */
    fun clearToken() { this.token = null }
}
