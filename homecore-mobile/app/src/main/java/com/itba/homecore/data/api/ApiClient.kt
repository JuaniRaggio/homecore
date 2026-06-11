package com.itba.homecore.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://hci.it.itba.edu.ar/api/"
    private const val API_KEY  = "sk_2ece0079ab8c2fb4fb03b5537aebf5b6"

    @Volatile
    private var token: String? = null

    fun setToken(t: String?) {
        this.token = t
    }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("X-API-Key", API_KEY)

                this@ApiClient.token?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }

                val response = chain.proceed(requestBuilder.build())

                // 401 ⇒ token vencido/inválido: avisar para limpiar sesión y volver a Login.
                if (response.code == 401) {
                    this@ApiClient.token = null
                    SessionEvents.emitUnauthorized()
                }
                response
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

    // Usamos tipos explícitos en los bloques lazy para resolver errores de inferencia
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
