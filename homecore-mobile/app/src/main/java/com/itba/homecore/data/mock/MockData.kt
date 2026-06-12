package com.itba.homecore.data.mock

import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceMeta
import com.itba.homecore.data.model.DeviceState
import com.itba.homecore.data.model.DeviceType
import com.itba.homecore.data.model.Room
import com.itba.homecore.data.model.RoomRef
import com.itba.homecore.data.model.Routine
import com.itba.homecore.data.model.RoutineMetadata

/**
 * Fuente de datos de prototipo. Mantiene listas mutables en memoria para que las
 * acciones (toggle, favoritos, ejecutar) se reflejen durante la sesión sin backend.
 *
 * Reproduce los mockups de la app: rutinas "Buenos días"/"Buenas noches"/"Riego
 * automático" y dispositivos favoritos "Lámpara principal"/"Puerta principal".
 */
object MockData {

    // Mutable: createRoom agrega habitaciones nuevas durante la sesión.
    val rooms: MutableList<Room> = mutableListOf(
        Room(id = "r1", name = "Living"),
        Room(id = "r2", name = "Dormitorio"),
        Room(id = "r3", name = "Cocina")
    )

    private fun ref(roomId: String) = rooms.first { it.id == roomId }
        .let { RoomRef(it.id, it.name) }

    // Lista mutable: las acciones mock la modifican en el lugar.
    val devices: MutableList<Device> = mutableListOf(
        Device(
            id = "d1", name = "Lámpara principal",
            type = DeviceType(name = "lamp"),
            state = DeviceState(status = "on", brightness = 80),
            room = ref("r1"),
            metadata = DeviceMeta(favorite = true)
        ),
        Device(
            id = "d2", name = "Puerta principal",
            type = DeviceType(name = "door"),
            state = DeviceState(status = "closed"),
            room = ref("r1"),
            metadata = DeviceMeta(favorite = true)
        ),
        Device(
            id = "d3", name = "Aire del living",
            type = DeviceType(name = "ac"),
            state = DeviceState(status = "off", temperature = 24, mode = "cool"),
            room = ref("r1"),
            metadata = DeviceMeta(favorite = false)
        ),
        Device(
            id = "d4", name = "Parlante",
            type = DeviceType(name = "speaker"),
            state = DeviceState(status = "off", volume = 30),
            room = ref("r1"),
            metadata = DeviceMeta(favorite = false)
        ),
        Device(
            id = "d5", name = "Lámpara de noche",
            type = DeviceType(name = "lamp"),
            state = DeviceState(status = "off", brightness = 40),
            room = ref("r2"),
            metadata = DeviceMeta(favorite = false)
        ),
        Device(
            id = "d6", name = "Persiana",
            type = DeviceType(name = "blinds"),
            state = DeviceState(status = "closed", level = 0),
            room = ref("r2"),
            metadata = DeviceMeta(favorite = false)
        ),
        Device(
            id = "d7", name = "Heladera",
            type = DeviceType(name = "refrigerator"),
            state = DeviceState(temperature = 4, freezerTemperature = -16, mode = "normal"),
            room = ref("r3"),
            metadata = DeviceMeta(favorite = false)
        ),
        Device(
            id = "d8", name = "Cerradura entrada",
            type = DeviceType(name = "lock"),
            state = DeviceState(status = "locked"),
            room = ref("r3"),
            metadata = DeviceMeta(favorite = false)
        )
    )

    val routines: MutableList<Routine> = mutableListOf(
        Routine(
            id = "rt1", name = "Buenos días",
            description = "Abre persianas y enciende luces suaves",
            metadata = RoutineMetadata(
                favorite = true, active = true,
                time = "7:30", days = listOf(1, 2, 3, 4, 5)
            )
        ),
        Routine(
            id = "rt2", name = "Buenas noches",
            description = "Cierra todo y activa alarma",
            metadata = RoutineMetadata(
                favorite = true, active = true,
                time = "22:30", days = listOf(1, 2, 3, 4, 5)
            )
        ),
        Routine(
            id = "rt3", name = "Riego automático",
            description = "Activa aspersores del jardín por 15 minutos",
            metadata = RoutineMetadata(
                favorite = false, active = false,
                time = "15:00", days = listOf(2, 4)
            )
        )
    )

    /** Estado inicial razonable para un dispositivo recién creado, según su tipo. */
    fun defaultStatusFor(typeName: String): String = when (typeName.lowercase()) {
        "door", "blinds", "faucet" -> "closed"
        "lock"                     -> "locked"
        "alarm"                    -> "off"
        "speaker"                  -> "stopped"
        "vacuum"                   -> "docked"
        else                        -> "off"
    }

    /** Traduce una acción a su estado resultante para reflejarlo en el prototipo. */
    fun statusForAction(action: String): String = when (action) {
        "turnOn"  -> "on"
        "turnOff" -> "off"
        "open"    -> "opened"
        "close"   -> "closed"
        "unlock"  -> "unlocked"
        "lock"    -> "locked"
        "armAway", "armStay" -> "armedaway"
        "disarm"  -> "off"
        "start"   -> "active"
        "pause", "dock" -> "off"
        "play"    -> "playing"
        "stop"    -> "off"
        else      -> action
    }
}
