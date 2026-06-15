package com.itba.homecore.data.model

/**
 * On/off-equivalent action pair for the device card's quick switch, mirroring the web
 * STATUS_MAP (curtain = up/down, speaker = play/stop, vacuum = start/dock, ...). The
 * full, bespoke per-type controls live in `ui/screens/devices/controls`.
 */
data class QuickToggle(val onAction: DeviceAction, val offAction: DeviceAction)

object DeviceCapabilities {

    /**
     * Quick-toggle pair for the card switch, or null when the type has no on/off concept
     * (e.g. fridge) so the card hides the switch.
     */
    fun quickToggle(category: DeviceCategory): QuickToggle? = when (category) {
        DeviceCategory.LAMP, DeviceCategory.AC, DeviceCategory.OVEN -> QuickToggle(DeviceAction.TURN_ON, DeviceAction.TURN_OFF)
        DeviceCategory.DOOR, DeviceCategory.FAUCET                  -> QuickToggle(DeviceAction.OPEN, DeviceAction.CLOSE)
        DeviceCategory.BLINDS                                       -> QuickToggle(DeviceAction.UP, DeviceAction.DOWN)
        DeviceCategory.ALARM                                        -> QuickToggle(DeviceAction.ARM_AWAY, DeviceAction.DISARM)
        DeviceCategory.SPEAKER                                      -> QuickToggle(DeviceAction.PLAY, DeviceAction.STOP)
        DeviceCategory.VACUUM                                       -> QuickToggle(DeviceAction.START, DeviceAction.DOCK)
        DeviceCategory.LOCK                                         -> QuickToggle(DeviceAction.UNLOCK, DeviceAction.LOCK)
        DeviceCategory.REFRIGERATOR, DeviceCategory.OTHER           -> null
    }
}
