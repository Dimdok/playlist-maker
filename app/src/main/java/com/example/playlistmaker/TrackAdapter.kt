package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String
)

class TrackAdapter(private val tracks: List<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val artwork: ImageView = itemView.findViewById(R.id.artworkImageView)
        private val trackName: TextView = itemView.findViewById(R.id.trackNameTextView)
        private val artistName: TextView = itemView.findViewById(R.id.artistNameTextView)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTimeTextView)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.trackTime

            // Загрузка обложки с Glide
            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.ic_cover_placeholder) // Плейсхолдер
                .transform(RoundedCorners(8)) // Закругление углов
                .into(artwork)
        }
    }
}