package com.lightfeather.data.local

import com.lightfeather.domain.model.UserData
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class AppPreferences(private val settings: Settings) {

    companion object{
        const val USER_DATA = "userData"
    }

    var userData : UserData? set(value) = settings.set(USER_DATA, value)
        get() = settings[USER_DATA]
}