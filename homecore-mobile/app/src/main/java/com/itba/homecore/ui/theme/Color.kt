package com.itba.homecore.ui.theme

import androidx.compose.ui.graphics.Color

// Design tokens — dark theme. Values mirror the web design system
// (homecore-web/src/assets/styles/variables.css) so both clients stay consistent.

// Backgrounds / surfaces
val Background      = Color(0xFF0F0F14) // bg-main
val Surface         = Color(0xFF1A1A24) // bg-card
val SurfaceVariant  = Color(0xFF252532) // bg-card-alt
val InputBackground = Color(0xFF3A3A5E) // bg-auth-input
val Border          = Color(0xFF3A3A4A) // border

// Accents
val Accent      = Color(0xFF818CF8) // accent
val AccentHover = Color(0xFFA5B4FC) // accent-hover
val AccentDark  = Color(0xFF4A52B8) // filled-button indigo (.btn)
val Amber       = Color(0xFFFBBF24) // amber (favorites / warning)

// Toggles
val ToggleOn  = Color(0xFF6A78F5) // toggle-on
val ToggleOff = Color(0xFF2E2E2E) // toggle-off

// Semantic
val ErrorColor   = Color(0xFFF87171) // danger (reserved for errors/destructive)
val SuccessColor = Color(0xFF34D399) // success

// Text
val TextPrimary   = Color(0xFFF1F5F9) // text-primary
val TextSecondary = Color(0xFFB0BDD0) // text-secondary
val TextMuted     = Color(0xFF8494A7) // text-muted
val OnAccent      = Color(0xFFFFFFFF) // text-on-accent

// Mobile-specific roles (no direct web token)
val FavoriteStar = Amber              // favorite star uses the shared amber
val LampIconBg   = Color(0xFF3A2A1A)  // dark amber tint behind the lamp icon

// Profile screen tokens
val AvatarBackground = Color(0xFF6C7080)
val PillBackground   = Color(0xFFE8E9F0)
val PillText         = Color(0xFF1F2030)

// Device type colors
val DeviceLight     = Color(0xFFF5A623)
val DeviceDoor      = Color(0xFF6C8EBF)
val DeviceAlarm     = Color(0xFFE05252)
val DeviceWater     = Color(0xFF4FC3F7)
val DeviceCurtain   = Color(0xFF81C784)
val DeviceAC        = Color(0xFFBA68C8)
val DeviceSpeaker   = Color(0xFFFF8A65)
val DeviceVacuum    = Color(0xFF90A4AE)
val DeviceFridge    = Color(0xFF4DD0E1)
val DeviceOven      = Color(0xFFFF7043)
val DeviceLock      = Color(0xFF2196F3)
val DeviceUnknown   = Color(0xFF78909C)
