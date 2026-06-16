package com.itba.homecore.ui.screens.devices

import com.itba.homecore.R
import com.itba.homecore.data.model.DeviceCategory

/**
 * Selectable option in the device creation flow. The API type key comes from
 * [DeviceCategory.typeName] so it is not duplicated here.
 */
data class DeviceTypeOption(
    val category: DeviceCategory,
    val labelRes: Int
) {
    val typeName: String get() = category.typeName
}

/** The 11 types supported by the HCI API. */
val selectableDeviceTypes: List<DeviceTypeOption> = listOf(
    DeviceTypeOption(DeviceCategory.LAMP,         R.string.dtype_lamp),
    DeviceTypeOption(DeviceCategory.DOOR,         R.string.dtype_door),
    DeviceTypeOption(DeviceCategory.ALARM,        R.string.dtype_alarm),
    DeviceTypeOption(DeviceCategory.FAUCET,       R.string.dtype_faucet),
    DeviceTypeOption(DeviceCategory.BLINDS,       R.string.dtype_blinds),
    DeviceTypeOption(DeviceCategory.AC,           R.string.dtype_ac),
    DeviceTypeOption(DeviceCategory.SPEAKER,      R.string.dtype_speaker),
    DeviceTypeOption(DeviceCategory.VACUUM,       R.string.dtype_vacuum),
    DeviceTypeOption(DeviceCategory.REFRIGERATOR, R.string.dtype_refrigerator),
    DeviceTypeOption(DeviceCategory.OVEN,         R.string.dtype_oven),
    DeviceTypeOption(DeviceCategory.LOCK,         R.string.dtype_lock)
)
