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
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var trackSearcher: TrackSearcher

    private var savedInputText: String? = null
    private var filteredTracks = mutableListOf<Track>()
    private var lastQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        showHistory(false)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchHistory = SearchHistory(this)

        // инициализация RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // инициализация адаптеров
        trackAdapter = TrackAdapter(filteredTracks)
        historyAdapter = TrackAdapter(searchHistory.getTracks())

        // установка начального адаптера
        recyclerView.adapter = trackAdapter

        // кнопка назад
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setOnClickListener {
            finish()
        }


        // инициализация элементов поиска
        val searchField = findViewById<EditText>(R.id.searchField)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)

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

        // обработка ввода текста
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) filteredTracks.clear() else clearIcon.visibility = View.VISIBLE
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
            showMessageLayout(MessageType.NONE)
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
        trackAdapter.setOnItemClickListener { track ->
            // сохраняем трек в списке истории
            searchHistory.saveTrack(track)
            historyAdapter.updateTracks(searchHistory.getTracks())

            openPlayer(track)
        }

        historyAdapter.setOnItemClickListener { track ->
            openPlayer(track)
        }

        trackSearcher = TrackSearcher(
            onSearchResult = { tracks ->
                filteredTracks.clear()
                filteredTracks.addAll(tracks)
                trackAdapter.notifyDataSetChanged()
                showMessageLayout(MessageType.NONE)
                showHistory(false)
                if (tracks.isEmpty()) {
                    hideKeyboard(searchField)
                    showMessageLayout(MessageType.NOT_FOUND)
                }
            },
            onSearchError = {
                lastQuery = searchField.text.toString()
                filteredTracks.clear()
                hideKeyboard(searchField)
                showMessageLayout(MessageType.ERROR)
            }
        )
    }

    // сохранение стейта инпута
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constants.INPUT_TEXT_KEY, savedInputText)
    }

    // восстановление стейта инпута после пересоздания Activity
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInputText = savedInstanceState.getString(Constants.INPUT_TEXT_KEY)
        findViewById<EditText>(R.id.searchField).setText(savedInputText)
        filterTracks(savedInputText ?: "")
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    // фильтрация треков по названию песни или исполнителя
    private fun filterTracks(query: String) {
        filteredTracks.clear()
        if (query.isEmpty()) {
            trackAdapter.notifyDataSetChanged()
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showHistory(isVisible: Boolean) {
        val visible = if (isVisible) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.historyTitle).visibility = visible
        findViewById<Button>(R.id.clearHistoryButton).visibility = visible
    }

    private fun retrySearch(query: String) {
        trackSearcher.searchTracks(query)
    }

    private fun showMessageLayout(type: MessageType) {
        when (type) {
            MessageType.ERROR -> {
                findViewById<LinearLayout>(R.id.notFoundLayout).visibility = View.GONE
                showHistory(false)
                findViewById<LinearLayout>(R.id.errorLayout).visibility = View.VISIBLE
            }

            MessageType.NOT_FOUND -> {
                findViewById<LinearLayout>(R.id.errorLayout).visibility = View.GONE
                showHistory(false)
                findViewById<LinearLayout>(R.id.notFoundLayout).visibility = View.VISIBLE
            }

            else -> {
                findViewById<LinearLayout>(R.id.errorLayout).visibility = View.GONE
                findViewById<LinearLayout>(R.id.notFoundLayout).visibility = View.GONE
            }
        }
    }

    private fun openPlayer(track: Track) {
        val audioPlayerIntent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra(Constants.ACTIVE_TRACK_KEY, track)
        }
        startActivity(audioPlayerIntent)
    }
}

enum class MessageType {
    NONE, ERROR, NOT_FOUND
}
