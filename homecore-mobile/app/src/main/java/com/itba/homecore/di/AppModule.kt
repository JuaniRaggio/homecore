package com.itba.homecore.di

import android.content.Context
import com.itba.homecore.data.repository.AuthRepository
import com.itba.homecore.data.repository.DevicesRepository
import com.itba.homecore.data.repository.MockAuthRepository
import com.itba.homecore.data.repository.MockDevicesRepository
import com.itba.homecore.data.repository.MockRoutinesRepository
import com.itba.homecore.data.repository.RemoteAuthRepository
import com.itba.homecore.data.repository.RemoteDevicesRepository
import com.itba.homecore.data.repository.RemoteRoutinesRepository
import com.itba.homecore.data.repository.RoutinesRepository

/**
 * Service locator: the SINGLE place where mock data vs real backend is decided.
 *
 * While [USE_MOCK] is `true` the whole app works with prototype data and never
 * touches the network. To connect the HCI API just set it to `false`: neither the
 * UI nor the ViewModels change, because they depend on the repository interfaces,
 * not on the concrete implementations.
 */
object AppModule {

    /** Set to `true` to work with prototype data instead of the real backend. */
    const val USE_MOCK = false

    val devicesRepository: DevicesRepository by lazy {
        if (USE_MOCK) MockDevicesRepository() else RemoteDevicesRepository()
    }

    val routinesRepository: RoutinesRepository by lazy {
        if (USE_MOCK) MockRoutinesRepository() else RemoteRoutinesRepository()
    }

    /** Auth needs a Context for SessionManager/DataStore in real mode. */
    fun authRepository(context: Context): AuthRepository =
        if (USE_MOCK) MockAuthRepository() else RemoteAuthRepository(context.applicationContext)
}
