package com.itba.homecore.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design tokens for dimensions, mirroring design-tokens.md so spacing, corner radius,
 * element sizes and text sizes are defined once instead of hardcoded per screen.
 * Stroke widths (1.dp borders) are left inline as they are a conventional constant.
 */

/** Spacing / padding / gaps. Scale from design-tokens.md (xs2 = 2xs, xl2..xl5 = 2xl..5xl). */
object Spacing {
    val xxs  = 2.dp
    val xs2  = 4.dp
    val xs   = 6.dp
    val sm   = 8.dp
    val md   = 10.dp
    val base = 12.dp
    val lg   = 14.dp
    val xl   = 16.dp
    val xl2  = 18.dp
    val xl3  = 20.dp
    val xl4  = 24.dp
    val xl5  = 28.dp
    val huge  = 32.dp
    val huge2 = 48.dp
    val huge3 = 64.dp
}

/** Corner radius. Base scale from design-tokens.md plus the card/pill sizes the UI uses. */
object Radius {
    val sm   = 6.dp
    val md   = 8.dp
    val lg   = 10.dp
    val xl   = 12.dp
    val xl2  = 14.dp
    val card = 16.dp
    val full = 20.dp
    val pill = 24.dp
    val round = 28.dp
}

/** Icon and small element sizes. */
object IconSize {
    val dot    = 8.dp
    val xs     = 14.dp
    val sm     = 18.dp
    val md     = 20.dp
    val lg     = 22.dp
    val xl     = 24.dp
    val box    = 28.dp
    val bell   = 36.dp
    val tile   = 40.dp
    val button = 48.dp
    val avatar = 80.dp
    val logo   = 84.dp
}

/** Text sizes (sp), mapped from design-tokens.md mobile scale. */
object TextSize {
    val sm       = 12.sp
    val base     = 13.sp
    val md       = 14.sp
    val lg       = 15.sp
    val xl       = 16.sp
    val xxl      = 18.sp
    val xxxl     = 20.sp
    val title    = 22.sp
    val headline = 24.sp
    val display  = 28.sp
}

/** Layout weights for Row/Column children, so call sites avoid the bare 1f literal. */
object Weight {
    const val Fill = 1f
}

/** Opacity values for borders, washes and disabled states (named to convey intent). */
object Alpha {
    const val opaque = 1f          
    const val disabled = 0.4f      
    const val hairlineBorder = 0.4f
    const val outlineBorder = 0.5f 
    const val strongBorder = 0.6f  
    const val chipBorder = 0.3f    
    const val tileBorder = 0.25f   
    const val iconWash = 0.18f     
    const val selectedWash = 0.15f 
}

/** Stroke widths used for borders and progress indicators. */
object Stroke {
    val hairline = 1.dp   // default 1dp border
    val selected = 2.dp   // emphasized border (selected tile)
    val indicator = 2.dp  // circular progress indicator
}

/** Line heights for compact card text (paired with TextSize.sm/lg). */
object LineHeight {
    val compact = 16.sp
    val normal = 18.sp
}

/** Scale factor applied to Material Switches so they fit the compact cards. */
const val SwitchScale = 0.85f

/** Curtain (blinds) open/close step, matching the web's 20% increments. */
const val CurtainStep = 20
