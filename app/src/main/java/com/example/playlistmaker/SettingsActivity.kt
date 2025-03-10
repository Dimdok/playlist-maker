package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appContext: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        appContext  = (applicationContext as App)
        sharedPreferences = getSharedPreferences(Constants.APP_SETTINGS_PREFS_KEY, MODE_PRIVATE)

        // кнопка назад
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setOnClickListener {
            finish()
        }

        // переключатель темной темы
        val themeSwitch: Switch = findViewById(R.id.themeSwitch)
        themeSwitch.isChecked = sharedPreferences.getBoolean(Constants.APP_SETTINGS_PREFS_KEY, false)

        themeSwitch.setOnCheckedChangeListener { _, checked ->
            appContext.switchTheme(checked)
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
            data = "mailto:${getString(R.string.support_email)}".toUri()
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
        }
        startActivity(Intent.createChooser(intent, getString(R.string.write_support)))
    }

    private fun openUserAgreement() {
        val url = getString(R.string.user_agreement_url)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = url.toUri()
        }
        startActivity(intent)
    }
}
