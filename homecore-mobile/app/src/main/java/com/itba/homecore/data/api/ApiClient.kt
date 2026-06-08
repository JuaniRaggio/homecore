package com.itba.homecore.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://hci.it.itba.edu.ar/api/"
    private const val API_KEY  = "sk_2ece0079ab8c2fb4fb03b5537aebf5b6"

    private var token: String? = null
    fun setToken(t: String?) { token = t }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .addHeader("X-API-Key", API_KEY)
                    .apply { token?.let { addHeader("Authorization", "Bearer $it") } }
                    .build()
                chain.proceed(req)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
}
