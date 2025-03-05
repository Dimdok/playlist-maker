package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class SearchHistory(context: Context) {
    private val prefsName = "searchHistory"
    private val prefsKey = "historyTracks"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

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
        val json = sharedPreferences.getString(prefsKey, null)
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
        sharedPreferences.edit() { putString(prefsKey, json) }
    }
}
