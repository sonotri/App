package com.example.guru2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SportsApiService {
    @GET("eventsday.php")
    fun getTodayEvents(
        @Query("d") date: String,
        @Query("l") league: String = "English Premier League"
    ): Call<SportsResponse>

    @GET("eventsnextleague.php")
    fun getUpcomingEvents(
        @Query("id") leagueId: String = "4328"
    ): Call<SportsResponse>

    @GET("searchteams.php")
    fun searchTeam(
        @Query("t") teamName: String
    ): Call<TeamResponse>
}

data class SportsResponse(
    val events: List<Match>?
)
