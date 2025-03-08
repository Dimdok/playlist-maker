package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// интерфейс для Retrofit
interface ItunesService {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<ItunesResponse>
}

data class TrackResult(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
)

data class ItunesResponse(
    val resultCount: Int,
    val results: List<TrackResult>
)

