package com.itba.homecore.data.api

import android.os.SystemClock
import android.util.Log
import com.itba.homecore.BuildConfig
import com.itba.homecore.data.model.DeviceAction
import com.itba.homecore.viewmodel.DeviceSyncEvents
import com.itba.homecore.viewmodel.NotificationEvents
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

/**
 * Socket.IO real-time client. Connects to the HCI host with the JWT + API key in the handshake
 * auth and reacts to device/home events pushed by the backend. On a device event it refreshes the
 * loaded screens via [DeviceSyncEvents] and (for external changes) posts a notification via
 * [NotificationEvents].
 *
 * The `deviceEvent` payload is `{ id, data: { deviceId, event, args } }` — it carries no device
 * name or status, so the name is resolved from [DeviceRegistry] and the action from [event].
 *
 * In-process only: connected while the app is running and logged in (see MainActivity wiring).
 */
object SocketManager {

    private const val TAG = "SocketManager"
    private const val TITLE = "HomeCore"

    // The WebSocket lives at the host root, not under /api/.
    private val WS_URL = BuildConfig.API_BASE_URL.substringBefore("/api")

    // The backend echoes the sender's own changes (and may emit an event more than once). We
    // suppress notifications for a device this client just acted on, and de-duplicate repeated
    // events for the same device within a short window. Both still refresh the UI.
    private const val SELF_ECHO_WINDOW_MS = 8_000L
    private const val DEDUP_WINDOW_MS = 2_000L

    private var socket: Socket? = null
    private val selfActedAt = HashMap<String, Long>()
    private val lastNotifiedAt = HashMap<String, Long>()
    @Volatile private var lastLocalActionAt = 0L

    /** Records that this client just mutated [deviceId] (null for create), so its echo won't notify. */
    fun markLocalActivity(deviceId: String?) {
        val now = SystemClock.elapsedRealtime()
        lastLocalActionAt = now
        if (deviceId != null) selfActedAt[deviceId] = now
    }

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

                on("deviceEvent") { args -> onDeviceEvent(args) }
                on("deviceCreated") { args -> onExternalDeviceChange(args, "Se agregó el dispositivo", "Se agregó un dispositivo") }
                on("deviceDeleted") { args -> onExternalDeviceChange(args, "Se eliminó el dispositivo", "Se eliminó un dispositivo") }
                // deviceUpdated only refreshes (deviceEvent already covers state-change notifications).
                on("deviceUpdated") { DeviceSyncEvents.emit() }
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
        selfActedAt.clear()
        lastNotifiedAt.clear()
    }

    private fun onDeviceEvent(args: Array<out Any?>) {
        DeviceSyncEvents.emit()
        val payload = args.firstOrNull() as? JSONObject ?: return
        val data = payload.optJSONObject("data")
        // The device id may be at the top level (id / deviceId / device.id) or inside data.
        val deviceId = firstNonBlank(
            payload.optString("id"),
            payload.optString("deviceId"),
            data?.optString("deviceId"),
            payload.optJSONObject("device")?.optString("id")
        ) ?: return
        val now = SystemClock.elapsedRealtime()

        // Skip this client's own change, and collapse duplicate emissions for the same device.
        if (now - (selfActedAt[deviceId] ?: 0L) < SELF_ECHO_WINDOW_MS) return
        if (now - (lastNotifiedAt[deviceId] ?: 0L) < DEDUP_WINDOW_MS) return
        lastNotifiedAt[deviceId] = now

        // Prefer the locally known name; fall back to a name embedded in the payload.
        val payloadName = payload.optJSONObject("device")?.optString("name")?.takeIf { it.isNotBlank() }
        val name = DeviceRegistry.nameFor(deviceId) ?: payloadName ?: "Un dispositivo"
        // The action can arrive as an explicit "event" string, or be inferred from the new state.
        val description = eventLabel(data?.optString("event").orEmpty()) ?: describeState(data)
        NotificationEvents.emit(TITLE, "$name: $description")
    }

    private fun onExternalDeviceChange(args: Array<out Any?>, withName: String, generic: String) {
        DeviceSyncEvents.emit()
        // No reliable deviceId for create/delete, so fall back to a global self-action window.
        if (SystemClock.elapsedRealtime() - lastLocalActionAt < SELF_ECHO_WINDOW_MS) return
        // Include the device name when the payload carries it (the backend sends `data.device`).
        val name = (args.firstOrNull() as? JSONObject)?.optJSONObject("device")
            ?.optString("name")?.takeIf { it.isNotBlank() }
        NotificationEvents.emit(TITLE, if (name != null) "$withName \"$name\"" else generic)
    }

    /**
     * Spanish description of a `deviceEvent` action, or null to fall back to a generic message.
     * Keyed by [DeviceAction] (not raw strings) so it stays aligned with the canonical action set.
     */
    private fun eventLabel(event: String): String? = when (DeviceAction.fromApi(event)) {
        DeviceAction.TURN_ON -> "Encendido"
        DeviceAction.TURN_OFF -> "Apagado"
        DeviceAction.OPEN -> "Abierta"
        DeviceAction.CLOSE -> "Cerrada"
        DeviceAction.LOCK -> "Bloqueada"
        DeviceAction.UNLOCK -> "Desbloqueada"
        DeviceAction.UP -> "Subida"
        DeviceAction.DOWN -> "Bajada"
        DeviceAction.PLAY, DeviceAction.RESUME -> "Reproduciendo"
        DeviceAction.PAUSE -> "Pausada"
        DeviceAction.STOP -> "Detenida"
        DeviceAction.START -> "Iniciada"
        DeviceAction.DOCK -> "En la base"
        DeviceAction.ARM_AWAY, DeviceAction.ARM_STAY -> "Activada"
        DeviceAction.DISARM -> "Desactivada"
        else -> null
    }

    /** Human description inferred from the new device state when there is no explicit event. */
    private fun describeState(state: JSONObject?): String {
        if (state == null) return "Estado actualizado"
        when (state.optString("status")) {
            "on" -> return "Encendido"
            "off" -> return "Apagado"
            "opened" -> return "Abierta"
            "closed" -> return "Cerrada"
            "active" -> return "Activada"
            "inactive" -> return "Desactivada"
            "playing" -> return "Reproduciendo"
        }
        when (state.optString("lock")) {
            "locked" -> return "Bloqueada"
            "unlocked" -> return "Desbloqueada"
        }
        if (state.has("brightness")) return "Brillo ${state.optInt("brightness")}%"
        if (state.has("temperature")) return "Temperatura ${state.optInt("temperature")}°"
        if (state.has("volume")) return "Volumen ${state.optInt("volume")}"
        if (state.has("level")) return "Nivel ${state.optInt("level")}%"
        return "Estado actualizado"
    }

    private fun firstNonBlank(vararg values: String?): String? =
        values.firstOrNull { !it.isNullOrBlank() }
}
