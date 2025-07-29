package com.example.guru2

import com.google.gson.annotations.SerializedName

data class TeamResponse(
    val teams: List<Team>?
)

data class Team(
    val strTeam: String?,                  // 팀 이름
    val idTeam: String?,
    val strStadium: String?,              // 구장 이름
    val strStadiumLocation: String?,      // 구장 위치
    val intStadiumCapacity: String?,      // 수용 인원
    val strStadiumThumb: String?          // 구장 썸네일 (이미지 URL)

)
