package com.example.guru2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
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

    @GET("searchplayers.php")
    fun getPlayersByTeam(
        @Query("t") teamName: String
    ): Call<PlayerResponse>

    @GET("eventspastleague.php")
    fun getPastEvents(
        @Query("id") leagueId: String = "4328" // EPL 고정
    ): Call<SportsResponse>

    @GET("lookup_all_players.php")
    fun getPlayers(@Query("id") teamId: String): Call<PlayerResponse>
}


data class SportsResponse(
    val events: List<Match>?
)

data class PlayerResponse(
    val player: List<Player>?
)

data class Player(
    val idPlayer: String?,
    val strPlayer: String?,
    val strPosition: String?,
    val dateBorn: String?,
    val strNationality: String?,
    val strCutout: String?
)


