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
 * Service locator: ÚNICO punto donde se decide entre datos mock y backend real.
 *
 * Mientras [USE_MOCK] sea `true`, toda la app trabaja con datos de prototipo y no
 * toca la red. Para conectar la API HCI basta con poner `false`: ni la UI ni los
 * ViewModels cambian, porque dependen de las interfaces de repositorio, no de las
 * implementaciones concretas.
 */
object AppModule {

    /** ⬅️ Cambiar a `true` para volver a datos mock (sin red). */
    const val USE_MOCK = false

    val devicesRepository: DevicesRepository by lazy {
        if (USE_MOCK) MockDevicesRepository() else RemoteDevicesRepository()
    }

    val routinesRepository: RoutinesRepository by lazy {
        if (USE_MOCK) MockRoutinesRepository() else RemoteRoutinesRepository()
    }

    /** Auth necesita Context para SessionManager/DataStore en el modo real. */
    fun authRepository(context: Context): AuthRepository =
        if (USE_MOCK) MockAuthRepository() else RemoteAuthRepository(context.applicationContext)
}
