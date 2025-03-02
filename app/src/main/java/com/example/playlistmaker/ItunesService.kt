package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class TrackResult(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val trackId: Int,
)

data class ItunesResponse(
    val resultCount: Int,
    val results: List<TrackResult>
)

// интерфейс для Retrofit
interface ItunesService {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<ItunesResponse>
}
