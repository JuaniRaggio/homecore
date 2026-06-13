package com.itba.homecore.di

import android.content.Context
import com.itba.homecore.data.repository.AuthRepository
import com.itba.homecore.data.repository.DevicesRepository
import com.itba.homecore.data.repository.RemoteAuthRepository
import com.itba.homecore.data.repository.RemoteDevicesRepository
import com.itba.homecore.data.repository.RemoteRoutinesRepository
import com.itba.homecore.data.repository.RoutinesRepository

object AppModule {

    val devicesRepository: DevicesRepository by lazy {
        RemoteDevicesRepository()
    }

    val routinesRepository: RoutinesRepository by lazy {
        RemoteRoutinesRepository()
    }

    fun authRepository(context: Context): AuthRepository =
        RemoteAuthRepository(context.applicationContext)
}
