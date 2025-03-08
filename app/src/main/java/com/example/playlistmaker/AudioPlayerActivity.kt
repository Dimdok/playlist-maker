package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class AudioPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.acivity_audio_player)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audioPlayer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val track = intent.getSerializableExtra(Constants.ACTIVE_TRACK_KEY) as Track


        val coverImage = findViewById<ImageView>(R.id.cover)
        val currentTime = findViewById<TextView>(R.id.currentTime)
        val buttonSave = findViewById<ImageButton>(R.id.buttonSave)
        val buttonPlay = findViewById<ImageButton>(R.id.buttonPlay)
        val buttonLike = findViewById<ImageButton>(R.id.buttonLike)

        // загрузка обложки
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_cover_placeholder_312) // плейсхолдер
            .transform(RoundedCorners(Helpers.dpToPix(8)))
            .into(coverImage)

        // данные трека
        findViewById<Group>(R.id.collectionGroup).visibility =
            if (track.collectionName.isEmpty()) View.GONE else View.VISIBLE

        findViewById<TextView>(R.id.trackName).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName
        findViewById<TextView>(R.id.trackTime).text = track.trackTime
        findViewById<TextView>(R.id.collectionName).text = track.collectionName
        findViewById<TextView>(R.id.releaseDate).text = track.releaseDate
        findViewById<TextView>(R.id.genre).text = track.primaryGenreName
        findViewById<TextView>(R.id.country).text = track.country

        // кнопка назад
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setOnClickListener {
            finish()
        }
    }
}