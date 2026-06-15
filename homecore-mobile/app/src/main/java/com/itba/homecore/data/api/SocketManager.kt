package com.itba.homecore.data.api

import android.util.Log
import com.itba.homecore.BuildConfig
import com.itba.homecore.viewmodel.DeviceSyncEvents
import com.itba.homecore.viewmodel.NotificationEvents
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

/**
 * Socket.IO real-time client. Mirrors the web's socket service: connects to the HCI host with
 * the JWT + API key in the handshake auth, and reacts to device/home events pushed by the
 * backend (changes made by other clients). On a device event it refreshes the loaded screens
 * via [DeviceSyncEvents] and posts a user notification via [NotificationEvents].
 *
 * In-process only: connected while the app is running and logged in (see MainActivity wiring).
 */
object SocketManager {

    private const val TAG = "SocketManager"

    // The WebSocket lives at the host root, not under /api/.
    private val WS_URL = BuildConfig.API_BASE_URL.substringBefore("/api")

    private var socket: Socket? = null

    // The server echoes the sender's own changes, so we suppress notifications (but still
    // refresh) for a short window after this client performed an action — only external
    // changes should notify. See [markLocalActivity].
    private const val SELF_ECHO_WINDOW_MS = 3_000L

    @Volatile
    private var lastLocalActionAt = 0L

    /** Call right before/after this client mutates a device, so its own echo doesn't notify. */
    fun markLocalActivity() {
        lastLocalActionAt = android.os.SystemClock.elapsedRealtime()
    }

    private fun isLocalEcho(): Boolean =
        android.os.SystemClock.elapsedRealtime() - lastLocalActionAt < SELF_ECHO_WINDOW_MS

    /** Connects with the session [token]; no-op if already connected. */
    fun connect(token: String) {
        if (socket != null) return
        val options = IO.Options().apply {
            transports = arrayOf("polling")
            auth = mapOf("token" to token, "apiKey" to BuildConfig.API_KEY)
        }
        socket = runCatching { IO.socket(WS_URL, options) }
            .onFailure { Log.e(TAG, "invalid socket URL", it) }
            .getOrNull()
            ?.apply {
                on(Socket.EVENT_CONNECT) { Log.d(TAG, "connected") }
                on(Socket.EVENT_DISCONNECT) { Log.d(TAG, "disconnected") }
                on(Socket.EVENT_CONNECT_ERROR) { Log.w(TAG, "connect error: ${it.firstOrNull()}") }

                // A device's state changed: refresh + notify (this is the state-change event).
                on("deviceEvent") { args -> onDeviceEvent(args) }
                // Created/deleted: refresh + notify.
                on("deviceCreated") { args -> notifyDevice(args, "Se agregó un dispositivo") }
                on("deviceDeleted") { args -> notifyDevice(args, "Se eliminó un dispositivo") }
                // Updated only refreshes (deviceEvent already notifies state changes; avoids duplicates).
                on("deviceUpdated") { DeviceSyncEvents.emit() }
                // Home sharing events.
                on("homeShared") { NotificationEvents.emit(TITLE, "Te compartieron un hogar") }
                on("homeUnshared") { NotificationEvents.emit(TITLE, "Dejaron de compartirte un hogar") }

                connect()
            }
    }

    fun disconnect() {
        socket?.apply {
            off()
            disconnect()
        }
        socket = null
    }

    private fun onDeviceEvent(args: Array<out Any?>) {
        DeviceSyncEvents.emit()
        if (isLocalEcho()) return
        val payload = args.firstOrNull() as? JSONObject
        val name = payload?.deviceName()
        val state = payload?.optJSONObject("data")?.let { stateText(it) }
        val message = when {
            name != null && state != null -> "$name: $state"
            name != null                  -> "$name cambió de estado"
            else                          -> "Un dispositivo cambió de estado"
        }
        NotificationEvents.emit(TITLE, message)
    }

    private fun notifyDevice(args: Array<out Any?>, fallback: String) {
        DeviceSyncEvents.emit()
        if (isLocalEcho()) return
        val name = (args.firstOrNull() as? JSONObject)?.deviceName()
        NotificationEvents.emit(TITLE, if (name != null) "$fallback: $name" else fallback)
    }

    /** Reads a device name from the common payload shapes ({device:{name}} or {name}). */
    private fun JSONObject.deviceName(): String? =
        optJSONObject("device")?.optString("name")?.takeIf { it.isNotBlank() }
            ?: optString("name").takeIf { it.isNotBlank() }

    /** Short Spanish description of a state payload, mirroring the web's describeEvent. */
    private fun stateText(state: JSONObject): String? = when {
        state.optString("status") == "on"       -> "Encendido"
        state.optString("status") == "off"      -> "Apagado"
        state.optString("status") == "opened"   -> "Abierta"
        state.optString("status") == "closed"   -> "Cerrada"
        state.optString("status") == "active"   -> "Activada"
        state.optString("status") == "playing"  -> "Reproduciendo"
        state.optString("lock") == "locked"     -> "Bloqueada"
        state.optString("lock") == "unlocked"   -> "Desbloqueada"
        else                                     -> null
    }

    private const val TITLE = "HomeCore"
}
