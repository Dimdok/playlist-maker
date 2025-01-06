package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // объявляем переменные для кнопок
        val searchButton = findViewById<Button>(R.id.search_button)
        val libraryButton = findViewById<Button>(R.id.library_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        // реализация клика по кнопке через анонимный класс
        val displaySearchIntent = Intent(this, SearchActivity::class.java)
        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(displaySearchIntent)
            }
        }
        searchButton.setOnClickListener(buttonClickListener)

        // реализация клика по кнопке через лямбда-выражение
        libraryButton.setOnClickListener {
            val displayIntent = Intent(this, LibraryActivity::class.java)
            startActivity(displayIntent)
        }

        settingsButton.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }
    }

}