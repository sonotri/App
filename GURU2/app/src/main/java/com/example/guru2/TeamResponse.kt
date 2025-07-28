package com.example.guru2

import com.google.gson.annotations.SerializedName

data class TeamResponse(
    val teams: List<Team>?
)

data class Team(
    @SerializedName("strTeam") val strTeam: String?,
    @SerializedName("strStadiumLatitude") val strStadiumLatitude: String?,
    @SerializedName("strStadiumLongitude") val strStadiumLongitude: String?,
    @SerializedName("strStadium") val strStadium: String?
)

