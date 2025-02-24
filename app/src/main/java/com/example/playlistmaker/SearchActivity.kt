package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SearchActivity : AppCompatActivity() {
    private var savedInputText: String? = null
    private val inputTextKey: String = "SAVED_INPUT_TEXT"
    private var filteredTracks = mutableListOf<Track>() // Отфильтрованные треки
    private lateinit var adapter: TrackAdapter
    private var lastQuery: String? = null // Сохраняем последний неудавшийся запрос

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
                clearIcon.isVisible = !s.isNullOrEmpty()
                savedInputText = s?.toString()
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
            hideKeyboard(inputEditText)
            filterTracks("")
            hideMessageLayouts()
        }

        // Обработка нажатия на кнопку Done
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = inputEditText.text.toString()
                searchTracks(query)
                true
            }
            false
        }

        // Обработка нажатия на кнопку "Обновить"
        val retryButton = findViewById<Button>(R.id.retryButton)
        retryButton.setOnClickListener {
            lastQuery?.let { retrySearch(it) }
        }
    }

    // Фильтрация треков по названию песни или исполнителя
    private fun filterTracks(query: String) {
        filteredTracks.clear()
        if (query.isEmpty()) {
            adapter.notifyDataSetChanged()
        }
    }


    // Поиск треков через API
    private fun searchTracks(query: String) {
        if (query.isEmpty()) {
            filterTracks("")
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ItunesService::class.java)
        service.search(query).enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(call: Call<ItunesResponse>, response: Response<ItunesResponse>) {
                if (response.isSuccessful) {
                    val tracks = response.body()?.results ?: emptyList()
                    filteredTracks.clear()
                    filteredTracks.addAll(tracks.map {
                        Track(
                            it.trackName,
                            it.artistName,
                            SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis),
                            it.artworkUrl100
                        )
                    })
                    if (filteredTracks.isEmpty()) {
                        showNotFoundLayout()
                    } else {
                        hideMessageLayouts()
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                lastQuery = query
                showErrorLayout()
            }
        })
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
        findViewById<FrameLayout>(R.id.errorLayout).visibility = View.VISIBLE
        findViewById<FrameLayout>(R.id.notFoundLayout).visibility = View.GONE
    }

    private fun showNotFoundLayout() {
        findViewById<FrameLayout>(R.id.errorLayout).visibility = View.GONE
        findViewById<FrameLayout>(R.id.notFoundLayout).visibility = View.VISIBLE
    }

    private fun hideMessageLayouts() {
        findViewById<FrameLayout>(R.id.errorLayout).visibility = View.GONE
        findViewById<FrameLayout>(R.id.notFoundLayout).visibility = View.GONE
    }

    private fun retrySearch(query: String) {
        searchTracks(query)
    }
}
