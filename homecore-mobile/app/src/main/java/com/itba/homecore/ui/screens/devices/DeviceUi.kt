package com.itba.homecore.ui.screens.devices

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Blinds
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Microwave
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.itba.homecore.R
import com.itba.homecore.data.model.DeviceCategory
import com.itba.homecore.ui.theme.*

/** Ícono Material por categoría de dispositivo. Único lugar (evita duplicación). */
fun deviceIconFor(cat: DeviceCategory): ImageVector = when (cat) {
    DeviceCategory.LAMP         -> Icons.Default.Lightbulb
    DeviceCategory.DOOR         -> Icons.Default.DoorFront
    DeviceCategory.ALARM        -> Icons.Default.Security
    DeviceCategory.FAUCET       -> Icons.Default.WaterDrop
    DeviceCategory.BLINDS       -> Icons.Default.Blinds
    DeviceCategory.AC           -> Icons.Default.AcUnit
    DeviceCategory.SPEAKER      -> Icons.Default.Speaker
    DeviceCategory.VACUUM       -> Icons.Default.CleaningServices
    DeviceCategory.REFRIGERATOR -> Icons.Default.Kitchen
    DeviceCategory.OVEN         -> Icons.Default.Microwave
    DeviceCategory.LOCK         -> Icons.Default.Lock
    else                        -> Icons.Default.DevicesOther
}

/** Color de acento por categoría (tomado de los design tokens). */
fun deviceColorFor(cat: DeviceCategory): Color = when (cat) {
    DeviceCategory.LAMP         -> DeviceLight
    DeviceCategory.DOOR         -> DeviceDoor
    DeviceCategory.ALARM        -> DeviceAlarm
    DeviceCategory.FAUCET       -> DeviceWater
    DeviceCategory.BLINDS       -> DeviceCurtain
    DeviceCategory.AC           -> DeviceAC
    DeviceCategory.SPEAKER      -> DeviceSpeaker
    DeviceCategory.VACUUM       -> DeviceVacuum
    DeviceCategory.REFRIGERATOR -> DeviceFridge
    DeviceCategory.OVEN         -> DeviceOven
    DeviceCategory.LOCK         -> DeviceLock
    else                        -> DeviceUnknown
}

/** Opción seleccionable en el alta de dispositivo. */
data class DeviceTypeOption(
    val category: DeviceCategory,
    val typeName: String,
    val labelRes: Int
)

/** Los 11 tipos soportados por la API HCI (mismas claves que homecore-web). */
val selectableDeviceTypes: List<DeviceTypeOption> = listOf(
    DeviceTypeOption(DeviceCategory.LAMP,         "lamp",         R.string.dtype_lamp),
    DeviceTypeOption(DeviceCategory.DOOR,         "door",         R.string.dtype_door),
    DeviceTypeOption(DeviceCategory.ALARM,        "alarm",        R.string.dtype_alarm),
    DeviceTypeOption(DeviceCategory.FAUCET,       "faucet",       R.string.dtype_faucet),
    DeviceTypeOption(DeviceCategory.BLINDS,       "blinds",       R.string.dtype_blinds),
    DeviceTypeOption(DeviceCategory.AC,           "ac",           R.string.dtype_ac),
    DeviceTypeOption(DeviceCategory.SPEAKER,      "speaker",      R.string.dtype_speaker),
    DeviceTypeOption(DeviceCategory.VACUUM,       "vacuum",       R.string.dtype_vacuum),
    DeviceTypeOption(DeviceCategory.REFRIGERATOR, "refrigerator", R.string.dtype_refrigerator),
    DeviceTypeOption(DeviceCategory.OVEN,         "oven",         R.string.dtype_oven),
    DeviceTypeOption(DeviceCategory.LOCK,         "lock",         R.string.dtype_lock)
)
