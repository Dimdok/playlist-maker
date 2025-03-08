package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    private var darkTheme = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        // на старте приложения считываем префы
        sharedPreferences = getSharedPreferences(Constants.APP_SETTINGS_PREFS_KEY, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(Constants.DARK_THEME_KEY, false)
        val isFirstRun = sharedPreferences.getBoolean(Constants.FIRST_RUN_KEY, true)

        if (isFirstRun) {
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_NO -> switchTheme(false)
                Configuration.UI_MODE_NIGHT_YES -> switchTheme(true)
            }
            // сохраняем, что первый запуск уже был
            val editor = sharedPreferences.edit()
            editor.putBoolean(Constants.FIRST_RUN_KEY, false)
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
        sharedPreferences.edit().putBoolean(Constants.DARK_THEME_KEY, darkThemeEnabled).apply()
    }
}