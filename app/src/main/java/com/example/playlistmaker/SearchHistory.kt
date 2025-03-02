package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(context: Context) {
    private val PREFS_NAME = "search_history"
    private val PREFS_KEY = "history_tracks"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveTrack(track: Track) {
        val tracks = getTracks().toMutableList()
        val existingTrack = tracks.find { it.trackId == track.trackId }
        if (existingTrack != null) {
            tracks.remove(existingTrack)
        }
        tracks.add(0, track)
        if (tracks.size > 10) {
            tracks.removeAt(10)
        }
        saveTracks(tracks)
    }

    fun getTracks(): List<Track> {
        val json = sharedPreferences.getString(PREFS_KEY, null)
        return if (json != null) {
            Gson().fromJson(json, object : TypeToken<List<Track>>() {}.type)
        } else {
            emptyList()
        }
    }

    fun clearHistory() {
        saveTracks(emptyList())
    }

    private fun saveTracks(tracks: List<Track>) {
        val json = Gson().toJson(tracks)
        sharedPreferences.edit().putString(PREFS_KEY, json).apply()
    }
}
