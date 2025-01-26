package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var themeSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // кнопка Вернуться назад
        val goBackButton = findViewById<Button>(R.id.go_back_button)
        goBackButton.setOnClickListener {
            finish()
        }

        // переключатель темной темы
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        themeSwitch = findViewById(R.id.themeSwitch)
        themeSwitch.isChecked = isDarkThemeEnabled()
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            setDarkTheme(isChecked)
        }

        // кнопка Поделиться приложением
        val shareAppButton = findViewById<TextView>(R.id.shareAppButton)
        shareAppButton.setOnClickListener {
            shareApp()
        }

        // кнопка Написать в поддержку
        val writeSupportButton = findViewById<TextView>(R.id.writeSupportButton)
        writeSupportButton.setOnClickListener {
            writeSupportEmail()
        }

        // кнопка Пользовательское соглашение
        val userAgreementButton = findViewById<TextView>(R.id.userAgreementButton)
        userAgreementButton.setOnClickListener {
            openUserAgreement()
        }
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.android_practicum_url))
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_app)))
    }

    private fun writeSupportEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${getString(R.string.support_email)}")
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
        }
        startActivity(Intent.createChooser(intent, getString(R.string.write_support)))
    }

    private fun openUserAgreement() {
        val url = getString(R.string.user_agreement_url)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

    private fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean("DarkTheme", false)
    }

    private fun setDarkTheme(enabled: Boolean) {
        val mode = if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
        sharedPreferences.edit().putBoolean("DarkTheme", enabled).apply()

        delegate.applyDayNight()
    }
}
