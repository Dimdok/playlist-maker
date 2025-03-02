package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    private lateinit var searchField: EditText
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory
    private lateinit var trackSearcher: TrackSearcher

    private var savedInputText: String? = null
    private val inputTextKey: String = "SAVED_INPUT_TEXT"
    private var filteredTracks = mutableListOf<Track>()
    private var lastQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        searchHistory = SearchHistory(this)
        showHistory(false)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // инициализация RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // инициализация адаптеров
        trackAdapter = TrackAdapter(filteredTracks)
        historyAdapter = TrackAdapter(searchHistory.getTracks())

        // установка начального адаптера
        recyclerView.adapter = trackAdapter

        fun showSearchHistory() {
            // проверяем есть ли треки в истории поиска
            val tracks = searchHistory.getTracks()

            if (searchField.hasFocus() && searchField.text.isEmpty() && tracks.isNotEmpty()) {
                historyAdapter.updateTracks(tracks)
                recyclerView.adapter = historyAdapter
                showHistory(true)
            } else {
                showHistory(false)
                recyclerView.adapter = trackAdapter
            }
        }

        // инициализация элементов поиска
        searchField = findViewById(R.id.searchField)
        val clearIcon: ImageView = findViewById(R.id.clearIcon)

        // кнопка Вернуться назад
        val goBackButton = findViewById<Button>(R.id.go_back_button)
        goBackButton.setOnClickListener {
            finish()
        }

        // обработка ввода текста
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) filteredTracks.clear() else clearIcon.isVisible
                savedInputText = s?.toString()
                showSearchHistory()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchField.setOnFocusChangeListener { _, hasFocus ->
            // показываем клавиатуру при фокусе инпута
            if (hasFocus) {
                showKeyboard(searchField)
                showSearchHistory()
            }
        }

        // очистка текста
        clearIcon.setOnClickListener {
            searchField.text.clear()
            savedInputText = null
            clearIcon.visibility = View.GONE
            hideKeyboard(searchField)
            filteredTracks.clear()
            hideMessageLayouts()
        }

        // обработка нажатия на кнопку Done
        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchField.text.toString()
                trackSearcher.searchTracks(query)
                true
            }
            false
        }

        // обработка нажатия на кнопку "Обновить"
        val retryButton = findViewById<Button>(R.id.retryButton)
        retryButton.setOnClickListener {
            lastQuery?.let { retrySearch(it) }
        }

        val clearHistoryButton: Button = findViewById(R.id.clearHistoryButton)
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            historyAdapter.updateTracks(emptyList())
            showHistory(false)
        }

        // обработка нажатия на трек в списке результатов поиска
        this.trackAdapter.setOnItemClickListener { track ->
            searchHistory.saveTrack(track)
            historyAdapter.updateTracks(searchHistory.getTracks())
        }

        trackSearcher = TrackSearcher(
            onSearchResult = { tracks ->
                filteredTracks.clear()
                filteredTracks.addAll(tracks)
                trackAdapter.notifyDataSetChanged()
                hideMessageLayouts()
                showHistory(false)
                if (tracks.isEmpty()) {
                    hideKeyboard(searchField)
                    showNotFoundLayout()
                }
            },
            onSearchError = {
                lastQuery = searchField.text.toString()
                filteredTracks.clear()
                hideKeyboard(searchField)
                showErrorLayout()
            }
        )
    }

    // фильтрация треков по названию песни или исполнителя
    private fun filterTracks(query: String) {
        filteredTracks.clear()
        if (query.isEmpty()) {
            trackAdapter.notifyDataSetChanged()
        }
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
        findViewById<EditText>(R.id.searchField).setText(savedInputText)
        filterTracks(savedInputText ?: "")
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showErrorLayout() {
        findViewById<LinearLayout>(R.id.notFoundLayout).visibility = View.GONE
        showHistory(false)
        findViewById<LinearLayout>(R.id.errorLayout).visibility = View.VISIBLE
    }

    private fun showNotFoundLayout() {
        findViewById<LinearLayout>(R.id.errorLayout).visibility = View.GONE
        showHistory(false)
        findViewById<LinearLayout>(R.id.notFoundLayout).visibility = View.VISIBLE
    }

    private fun hideMessageLayouts() {
        findViewById<LinearLayout>(R.id.errorLayout).visibility = View.GONE
        findViewById<LinearLayout>(R.id.notFoundLayout).visibility = View.GONE
    }

    private fun showHistory(isVisible: Boolean) {
        val visible = if (isVisible) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.historyTitle).visibility = visible
        findViewById<Button>(R.id.clearHistoryButton).visibility = visible
    }

    private fun retrySearch(query: String) {
        trackSearcher.searchTracks(query)
    }
}
