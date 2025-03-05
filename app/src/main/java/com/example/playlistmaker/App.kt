package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    val darkThemeKey: String = "darkThemeKey"
    val appSettingsPrefsName: String = "playlistMakerSettings"

    private var darkTheme = false
    private val firstRunKey = "firstRun"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        // на старте приложения считываем префы
        sharedPreferences = getSharedPreferences(appSettingsPrefsName, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(darkThemeKey, false)
        val isFirstRun = sharedPreferences.getBoolean(firstRunKey, true)

        if (isFirstRun) {
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_NO -> switchTheme(false)
                Configuration.UI_MODE_NIGHT_YES -> switchTheme(true)
            }
            // сохраняем, что первый запуск уже был
            val editor = sharedPreferences.edit()
            editor.putBoolean(firstRunKey, false)
            editor.apply()
        } else {
            switchTheme(darkTheme)
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        // сохраняем тему в настройках при переключении
        sharedPreferences.edit().putBoolean(darkThemeKey, darkThemeEnabled).apply()
    }
}