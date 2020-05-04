package com.example.stockticker.ticker.settings

import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener

internal open class DefaultPreferenceChangeListener :
    OnPreferenceChangeListener {

    override fun onPreferenceChange(
        preference: Preference,
        newValue: Any
    ) = false
}