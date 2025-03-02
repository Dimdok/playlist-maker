package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    private var darkTheme = false
    val appSettingsPrefsName: String = "playlist_maker_settings"
    val darkThemeKey: String = "dark_theme_key"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        // на старте приложения считываем префы
        sharedPreferences = getSharedPreferences(appSettingsPrefsName, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(darkThemeKey, false)
        // активируем сохранённую в настройках тему
        switchTheme(darkTheme)
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