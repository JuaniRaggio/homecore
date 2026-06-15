package com.itba.homecore.util

import android.util.Log
import com.itba.homecore.data.model.Routine
import com.itba.homecore.data.model.days
import com.itba.homecore.data.model.isActive
import com.itba.homecore.data.model.time
import com.itba.homecore.di.AppModule
import com.itba.homecore.viewmodel.NotificationEvents
import com.itba.homecore.viewmodel.RoutineExecutionEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Fires scheduled routines at their configured time + day. Polls every 30 s while
 * the app process is alive; for an active routine whose time (HH:mm) matches the
 * current hour:minute and whose day list contains today (0=Sun..6=Sat), it calls
 * the routines execute endpoint and posts a system notification.
 *
 * In-app scheduler: only runs while the app is in memory. Background execution
 * would need WorkManager/AlarmManager + a foreground service.
 */
object RoutineScheduler {

    private const val TAG = "RoutineScheduler"
    private const val POLL_INTERVAL_MS = 30_000L

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null

    // Avoid firing the same routine twice within the same minute (we poll faster than once a minute).
    private val lastFiredAt = mutableMapOf<String, String>()

    /** Idempotent: starts the scheduler if it is not already running. */
    fun start() {
        if (job?.isActive == true) return
        Log.d(TAG, "start")
        job = scope.launch {
            while (isActive) {
                runCatching { tick() }.onFailure { Log.e(TAG, "tick failed", it) }
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    fun stop() {
        Log.d(TAG, "stop")
        job?.cancel()
        job = null
        lastFiredAt.clear()
    }

    private suspend fun tick() {
        val cal = Calendar.getInstance()
        val nowDay = (cal.get(Calendar.DAY_OF_WEEK) - 1)             // Sun=0 .. Sat=6
        val nowTime = "%02d:%02d".format(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
        val stamp = "%04d-%02d-%02d %s".format(
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), nowTime
        )

        val routines = AppModule.routinesRepository.getRoutines().getOrNull().orEmpty()
        Log.d(TAG, "tick day=$nowDay time=$nowTime routines=${routines.size}")
        for (r in routines) {
            val rTime = normalizeTime(r.time())
            val rDays = r.days()
            Log.d(
                TAG,
                "  '${r.name}' active=${r.isActive()} time=$rTime days=$rDays match=${rTime == nowTime && nowDay in rDays}"
            )
            if (!r.isActive()) continue
            if (rTime.isBlank() || rTime != nowTime) continue
            if (nowDay !in rDays) continue
            if (lastFiredAt[r.id] == stamp) continue
            fire(r, stamp)
        }
    }

    /** Tolerates "H:mm", "HH:mm", or "HH:mm:ss" by zero-padding the hour and trimming seconds. */
    private fun normalizeTime(raw: String): String {
        val s = raw.trim()
        if (s.isBlank()) return ""
        val parts = s.split(":")
        if (parts.size < 2) return s
        val hh = parts[0].trim().padStart(2, '0')
        val mm = parts[1].trim().padStart(2, '0').take(2)
        return "$hh:$mm"
    }

    private suspend fun fire(r: Routine, stamp: String) {
        Log.d(TAG, "fire '${r.name}' id=${r.id}")
        lastFiredAt[r.id] = stamp
        val result = AppModule.routinesRepository.executeRoutine(r.id)
        if (result.isSuccess) {
            NotificationEvents.emit("HomeCore", executedMessage(r))
            RoutineExecutionEvents.emit()
        } else {
            Log.w(TAG, "execute failed for ${r.id}", result.exceptionOrNull())
            // Allow a retry next minute if the API call failed.
            lastFiredAt.remove(r.id)
        }
    }

    /** "Se ejecutó la rutina 'X' en 'Casa Y'", omitting the home if it can't be resolved. */
    private suspend fun executedMessage(r: Routine): String {
        val homeName = r.metadata?.homeId?.let { homeId ->
            AppModule.homesRepository.getHomes().getOrNull()
                ?.firstOrNull { it.id == homeId }?.name
        }
        return if (homeName != null) "Se ejecutó la rutina '${r.name}' en '$homeName'"
               else "Se ejecutó la rutina '${r.name}'"
    }
}
