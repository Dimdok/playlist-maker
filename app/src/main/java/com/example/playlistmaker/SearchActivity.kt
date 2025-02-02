package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    private var savedInputText: String? = null
    private val inputTextKey: String = "SAVED_INPUT_TEXT"
    private val allTracks = createTestTracks() // Все треки
    private var filteredTracks = mutableListOf<Track>() // Отфильтрованные треки
    private lateinit var adapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Инициализация RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(filteredTracks)
        recyclerView.adapter = adapter

        // Инициализация элементов поиска
        val inputEditText: EditText = findViewById(R.id.inputEditText)
        val clearIcon: ImageView = findViewById(R.id.clearIcon)

        // кнопка Вернуться назад
        val goBackButton = findViewById<Button>(R.id.go_back_button)
        goBackButton.setOnClickListener {
            finish()
        }

        // Обработка ввода текста
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // поменял. если я правильно понял как использовать isVisible
                clearIcon.isVisible = !s.isNullOrEmpty()
                savedInputText = s?.toString()
                filterTracks(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // показываем клавиатуру при фокусе инпута
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) showKeyboard(inputEditText)
        }

        // Очистка текста
        clearIcon.setOnClickListener {
            inputEditText.text.clear()
            savedInputText = null
            clearIcon.visibility = View.GONE
            hideKeyboard(inputEditText) // прячем клавиатуру
            filterTracks("") // Очищаем список треков
        }
    }

    // Фильтрация треков по названию песни или исполнителя
    private fun filterTracks(query: String) {
        filteredTracks.clear()
        if (query.isEmpty()) {
            filteredTracks.addAll(emptyList())
        } else {
            for (track in allTracks) {
                if (track.trackName.contains(query, true) || track.artistName.contains(query, true)) {
                    filteredTracks.add(track)
                }
            }
        }

        adapter.notifyDataSetChanged() // обновляем  весь список
    }

    // сохранение стейта инпута
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(inputTextKey, savedInputText)
    }

    // восстановление стейта инпута после пересоздания Activity
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInputText = savedInstanceState.getString(inputTextKey)
        findViewById<EditText>(R.id.inputEditText).setText(savedInputText)
        filterTracks(savedInputText ?: "") // Восстанавливаем фильтрацию
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // моковые данные
    private fun createTestTracks(): List<Track> {
        return listOf(
            /*Track(
                "Nirvana Длинное название трека для проверки переполнения",
                "Константинопольский Константин Константинопольский",
                "5:01",
                "проверка плейсхолдера"
            ),*/
            Track(
                "Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Billie Jean",
                "Michael Jackson",
                "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            ),
            Track(
                "Stayin' Alive",
                "Bee Gees",
                "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            ),
            Track(
                "Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )
    }
}