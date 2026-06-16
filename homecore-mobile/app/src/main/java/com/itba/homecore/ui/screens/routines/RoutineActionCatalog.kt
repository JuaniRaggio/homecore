package com.itba.homecore.ui.screens.routines

import com.itba.homecore.R
import com.itba.homecore.data.model.AcFanSpeed
import com.itba.homecore.data.model.AcMode
import com.itba.homecore.data.model.DeviceAction
import com.itba.homecore.data.model.DeviceCategory
import com.itba.homecore.data.model.DispenseUnit
import com.itba.homecore.data.model.FridgeMode
import com.itba.homecore.data.model.OvenConvection
import com.itba.homecore.data.model.OvenGrill
import com.itba.homecore.data.model.OvenHeat
import com.itba.homecore.data.model.SpeakerGenre
import com.itba.homecore.data.model.VacuumMode

/** One option of a [ParamField.Choice]; [labelRes] null shows the raw API [value]. */
data class SelectOption(val value: String, val labelRes: Int? = null)

/** A parameter the user fills in for a routine action. */
sealed interface ParamField {
    val labelRes: Int

    /** Integer in [min]..[max] (stepped), shown with [unit]; defaults to [default]. */
    data class Num(override val labelRes: Int, val min: Int, val max: Int, val step: Int = 1, val unit: String = "", val default: Int = min) : ParamField

    /** One value from a fixed set (mode, genre, fan speed, unit, ...). */
    data class Choice(override val labelRes: Int, val options: List<SelectOption>) : ParamField

    /** A 4-digit numeric code (alarm). */
    data class Code(override val labelRes: Int) : ParamField

    /** A preset color swatch (lamp), emitted as a hex string. */
    data class ColorPick(override val labelRes: Int) : ParamField

    /** A room of the current home (vacuum location); emitted as the room id. */
    data class Rooms(override val labelRes: Int) : ParamField
}

/** An action offered in the routine editor, with the parameters it needs (empty = runs as-is). */
data class RoutineActionSpec(val action: DeviceAction, val params: List<ParamField> = emptyList())

private fun spec(action: DeviceAction, vararg params: ParamField) = RoutineActionSpec(action, params.toList())

private fun choice(labelRes: Int, vararg options: Pair<String, Int>): ParamField.Choice =
    ParamField.Choice(labelRes, options.map { SelectOption(it.first, it.second) })

private fun rawChoice(labelRes: Int, values: List<String>): ParamField.Choice =
    ParamField.Choice(labelRes, values.map { SelectOption(it) })

/**
 * The full set of actions a routine can run on a given device type, with their parameters.
 * Mirrors the per-type controls in `devices/controls`, so routines can do everything the
 * device detail can (not just on/off).
 */
fun routineActionsFor(cat: DeviceCategory): List<RoutineActionSpec> = when (cat) {
    DeviceCategory.LAMP -> listOf(
        spec(DeviceAction.TURN_ON),
        spec(DeviceAction.TURN_OFF),
        spec(DeviceAction.SET_BRIGHTNESS, ParamField.Num(R.string.act_brightness, 0, 100, unit = "%", default = 100)),
        spec(DeviceAction.SET_COLOR, ParamField.ColorPick(R.string.act_color))
    )
    DeviceCategory.DOOR -> listOf(
        spec(DeviceAction.OPEN), spec(DeviceAction.CLOSE), spec(DeviceAction.LOCK), spec(DeviceAction.UNLOCK)
    )
    DeviceCategory.LOCK -> listOf(spec(DeviceAction.LOCK), spec(DeviceAction.UNLOCK))
    DeviceCategory.ALARM -> listOf(
        spec(DeviceAction.ARM_AWAY, ParamField.Code(R.string.act_security_code)),
        spec(DeviceAction.ARM_STAY, ParamField.Code(R.string.act_security_code)),
        spec(DeviceAction.DISARM, ParamField.Code(R.string.act_security_code))
    )
    DeviceCategory.FAUCET -> listOf(
        spec(DeviceAction.OPEN), spec(DeviceAction.CLOSE),
        spec(
            DeviceAction.DISPENSE,
            ParamField.Num(R.string.act_amount, 1, 100, default = 1),
            ParamField.Choice(R.string.act_unit, listOf(
                SelectOption(DispenseUnit.ML, R.string.unit_ml),
                SelectOption(DispenseUnit.CL, R.string.unit_cl),
                SelectOption(DispenseUnit.DL, R.string.unit_dl),
                SelectOption(DispenseUnit.L, R.string.unit_l)
            ))
        )
    )
    DeviceCategory.BLINDS -> listOf(
        spec(DeviceAction.UP), spec(DeviceAction.DOWN),
        spec(DeviceAction.SET_LEVEL, ParamField.Num(R.string.act_position, 0, 100, unit = "%"))
    )
    DeviceCategory.AC -> listOf(
        spec(DeviceAction.TURN_ON),
        spec(DeviceAction.TURN_OFF),
        spec(DeviceAction.SET_TEMPERATURE, ParamField.Num(R.string.act_temperature, 18, 38, unit = "°C", default = 24)),
        spec(DeviceAction.SET_MODE, choice(R.string.act_mode,
            AcMode.COOL to R.string.ac_mode_cool, AcMode.HEAT to R.string.ac_mode_heat, AcMode.FAN to R.string.ac_mode_fan)),
        spec(DeviceAction.SET_FAN_SPEED, rawChoice(R.string.act_fan_speed, AcFanSpeed.all))
    )
    DeviceCategory.SPEAKER -> listOf(
        spec(DeviceAction.PLAY), spec(DeviceAction.PAUSE), spec(DeviceAction.STOP),
        spec(DeviceAction.RESUME), spec(DeviceAction.NEXT_SONG), spec(DeviceAction.PREVIOUS_SONG),
        spec(DeviceAction.SET_VOLUME, ParamField.Num(R.string.act_volume, 0, 10, default = 5)),
        spec(DeviceAction.SET_GENRE, choice(R.string.act_genre,
            SpeakerGenre.CLASSICAL to R.string.genre_classical, SpeakerGenre.COUNTRY to R.string.genre_country,
            SpeakerGenre.DANCE to R.string.genre_dance, SpeakerGenre.LATINA to R.string.genre_latina,
            SpeakerGenre.POP to R.string.genre_pop, SpeakerGenre.ROCK to R.string.genre_rock))
    )
    DeviceCategory.VACUUM -> listOf(
        spec(DeviceAction.START), spec(DeviceAction.PAUSE), spec(DeviceAction.DOCK),
        spec(DeviceAction.SET_MODE, choice(R.string.act_mode,
            VacuumMode.VACUUM to R.string.vacuum_mode_vacuum, VacuumMode.MOP to R.string.vacuum_mode_mop)),
        spec(DeviceAction.SET_LOCATION, ParamField.Rooms(R.string.act_location))
    )
    DeviceCategory.REFRIGERATOR -> listOf(
        spec(DeviceAction.SET_TEMPERATURE, ParamField.Num(R.string.act_temperature, 2, 8, unit = "°C", default = 4)),
        spec(DeviceAction.SET_FREEZER_TEMPERATURE, ParamField.Num(R.string.act_freezer_temp, -20, -8, unit = "°C", default = -16)),
        spec(DeviceAction.SET_MODE, choice(R.string.act_mode,
            FridgeMode.DEFAULT to R.string.fridge_mode_default, FridgeMode.VACATION to R.string.fridge_mode_vacation,
            FridgeMode.PARTY to R.string.fridge_mode_party))
    )
    DeviceCategory.OVEN -> listOf(
        spec(DeviceAction.TURN_ON),
        spec(DeviceAction.TURN_OFF),
        spec(DeviceAction.SET_TEMPERATURE, ParamField.Num(R.string.act_temperature, 90, 230, step = 10, unit = "°C", default = 90)),
        spec(DeviceAction.SET_HEAT, choice(R.string.act_heat_source,
            OvenHeat.CONVENTIONAL to R.string.oven_heat_conventional, OvenHeat.BOTTOM to R.string.oven_heat_bottom,
            OvenHeat.TOP to R.string.oven_heat_top)),
        spec(DeviceAction.SET_GRILL, choice(R.string.act_grill,
            OvenGrill.LARGE to R.string.oven_grill_large, OvenGrill.ECO to R.string.oven_grill_eco,
            OvenGrill.OFF to R.string.oven_grill_off)),
        spec(DeviceAction.SET_CONVECTION, choice(R.string.act_convection,
            OvenConvection.NORMAL to R.string.oven_conv_normal, OvenConvection.ECO to R.string.oven_conv_eco,
            OvenConvection.OFF to R.string.oven_conv_off))
    )
    DeviceCategory.OTHER -> emptyList()
}
