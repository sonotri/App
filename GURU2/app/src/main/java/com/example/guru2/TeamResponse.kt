package com.example.guru2

data class TeamResponse(
    val teams: List<Team>?
)

data class Team(
    val strTeam: String?,
    val strStadium: String?,
    val strStadiumLocation: String?,
    val strStadiumLatitude: String?,
    val strStadiumLongitude: String?
)
