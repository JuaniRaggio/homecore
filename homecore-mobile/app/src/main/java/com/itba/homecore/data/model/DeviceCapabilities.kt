package com.itba.homecore.data.model

/**
 * On/off-equivalent action pair for the device card's quick switch, mirroring the web
 * STATUS_MAP (curtain = up/down, speaker = play/stop, vacuum = start/dock, ...). The
 * full, bespoke per-type controls live in `ui/screens/devices/controls`.
 */
data class QuickToggle(val onAction: String, val offAction: String)

object DeviceCapabilities {

    /**
     * Quick-toggle pair for the card switch, or null when the type has no on/off concept
     * (e.g. fridge) so the card hides the switch.
     */
    fun quickToggle(category: DeviceCategory): QuickToggle? = when (category) {
        DeviceCategory.LAMP, DeviceCategory.AC, DeviceCategory.OVEN -> QuickToggle("turnOn", "turnOff")
        DeviceCategory.DOOR, DeviceCategory.FAUCET                  -> QuickToggle("open", "close")
        DeviceCategory.BLINDS                                       -> QuickToggle("up", "down")
        DeviceCategory.ALARM                                        -> QuickToggle("armAway", "disarm")
        DeviceCategory.SPEAKER                                      -> QuickToggle("play", "stop")
        DeviceCategory.VACUUM                                       -> QuickToggle("start", "dock")
        DeviceCategory.LOCK                                         -> QuickToggle("unlock", "lock")
        DeviceCategory.REFRIGERATOR, DeviceCategory.OTHER           -> null
    }
}
