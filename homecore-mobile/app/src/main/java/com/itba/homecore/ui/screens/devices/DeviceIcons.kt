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
import com.itba.homecore.data.model.DeviceCategory
import com.itba.homecore.ui.theme.*

/** Material icon per device category. Single place (avoids duplication). */
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

/** Accent color per category (taken from the design tokens). */
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
