package com.itba.homecore.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Single entry point for the in-app language. Wraps AppCompat's per-app locale API so the
 * UI never touches AppCompatDelegate directly. The chosen locale is applied immediately and
 * persisted by AppCompat (see the manifest service + localeConfig).
 */
object LocaleManager {

    /** Languages the app ships translations for. [tag] is the BCP-47 language tag. */
    enum class Language(val tag: String) {
        SPANISH("es"),
        ENGLISH("en");

        companion object {
            /** Maps a language tag (e.g. "en-US") to a supported [Language], defaulting to Spanish. */
            fun fromTag(tag: String?): Language =
                entries.firstOrNull { tag?.startsWith(it.tag) == true } ?: SPANISH
        }
    }

    /** Applies [language] app-wide and persists it. */
    fun set(language: Language) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.tag))
    }
}
