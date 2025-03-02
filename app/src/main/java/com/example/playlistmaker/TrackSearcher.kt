package com.example.playlistmaker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class TrackSearcher(private val onSearchResult: (List<Track>) -> Unit, private val onSearchError: () -> Unit) {

    fun searchTracks(query: String) {
        if (query.isEmpty()) {
            onSearchResult(emptyList())
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
                    val filteredTracks = tracks.map {
                        Track(
                            it.trackName,
                            it.artistName,
                            SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis),
                            it.artworkUrl100,
                            it.trackId
                        )
                    }
                    onSearchResult(filteredTracks)
                } else {
                    onSearchError()
                }
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                onSearchError()
            }
        })
    }
}
