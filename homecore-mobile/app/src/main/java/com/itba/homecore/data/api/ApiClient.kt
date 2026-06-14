package com.itba.homecore.data.api

import com.google.gson.JsonParser
import com.itba.homecore.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Injected at build time from local.properties / env vars (see app/build.gradle.kts).
    private val BASE_URL: String = BuildConfig.API_BASE_URL
    private val API_KEY: String = BuildConfig.API_KEY

    @Volatile
    private var token: String? = null

    fun setToken(t: String?) {
        this.token = t
    }

    /** True while an auth token is held in memory. The 401 interceptor clears it. */
    fun hasToken(): Boolean = token != null

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                // Full bodies only in debug: release logs would leak the JWT and user data.
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE
            })
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("X-API-Key", API_KEY)

                this@ApiClient.token?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }

                val response = chain.proceed(requestBuilder.build())

                // 401 means the token expired or is invalid: notify so the session is
                // cleared and the app navigates back to Login.
                if (response.code == 401) {
                    this@ApiClient.token = null
                    SessionEvents.emitUnauthorized()
                }
                unwrapResult(response)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * The HCI API wraps successful responses in { "result": ... }. Mirroring what the
     * web app does in client.js, the payload is unwrapped in this single place so the
     * Retrofit interfaces can declare plain types (List<Device>, AuthResponse, etc.).
     * Bodies that are not JSON or have no "result" key pass through unchanged.
     * Note: the logging interceptor prints the already-unwrapped body.
     */
    private fun unwrapResult(response: Response): Response {
        if (!response.isSuccessful) return response
        val body = response.body ?: return response
        val contentType = body.contentType()
        if (contentType?.subtype?.contains("json") != true) return response

        val raw = body.string()
        val out = try {
            if (raw.isBlank()) raw
            else {
                val json = JsonParser.parseString(raw)
                if (json.isJsonObject && json.asJsonObject.has("result"))
                    json.asJsonObject.get("result").toString()
                else raw
            }
        } catch (_: Exception) {
            raw
        }
        return response.newBuilder().body(out.toResponseBody(contentType)).build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Explicit types on the lazy blocks avoid type-inference errors
    val authApi: AuthApi by lazy<AuthApi> { 
        retrofit.create(AuthApi::class.java) 
    }
    
    val devicesApi: DevicesApi by lazy<DevicesApi> { 
        retrofit.create(DevicesApi::class.java) 
    }
    
    val roomsApi: RoomsApi by lazy<RoomsApi> { 
        retrofit.create(RoomsApi::class.java) 
    }
    
    val homesApi: HomesApi by lazy<HomesApi> { 
        retrofit.create(HomesApi::class.java) 
    }
    
    val routinesApi: RoutinesApi by lazy<RoutinesApi> { 
        retrofit.create(RoutinesApi::class.java) 
    }
}
