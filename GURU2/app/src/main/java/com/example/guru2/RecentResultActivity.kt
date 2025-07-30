package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecentResultActivity : AppCompatActivity() {

    private lateinit var teamName: String
    private lateinit var titleText: TextView
    private lateinit var resultContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_result)

        teamName = intent.getStringExtra("teamName") ?: "Unknown Team"
        titleText = findViewById(R.id.recentResultTitle)
        resultContainer = findViewById(R.id.recentResultContainer)

        titleText.text = "$teamName - 최근 경기 결과"

        fetchRecentResults()

        // 하단 메뉴 버튼 연결
        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        val btnSchedule = findViewById<ImageButton>(R.id.btn_schedule)
        val btnPlayer = findViewById<ImageButton>(R.id.btn_player)
        val btnLocation = findViewById<ImageButton>(R.id.btn_location)
        val btnProfile = findViewById<ImageButton>(R.id.btn_profile)

        btnHome.setOnClickListener {}
        btnSchedule.setOnClickListener {
            startActivity(Intent(this, ScheduleActivity::class.java))
        }
        btnPlayer.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
        btnLocation.setOnClickListener {
            startActivity(Intent(this, LocationActivity::class.java))
        }
        btnProfile.setOnClickListener {
            startActivity(Intent(this, MypageActivity::class.java))
        }
        btnHome.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun fetchRecentResults() {
        RetrofitClient.api.getSeasonEvents(season = "2024-2025")
            .enqueue(object : Callback<SportsResponse> {
                override fun onResponse(
                    call: Call<SportsResponse>,
                    response: Response<SportsResponse>
                ) {
                    if (response.isSuccessful) {
                        val allMatches = response.body()?.events ?: emptyList()

                        val recent = allMatches
                            .filter {
                                val home = it.strHomeTeam?.lowercase()?.replace(" ", "") ?: ""
                                val away = it.strAwayTeam?.lowercase()?.replace(" ", "") ?: ""
                                val normalized = teamName.lowercase().replace(" ", "")
                                home.contains(normalized) || away.contains(normalized)
                            }
                            .take(5)

                        val matchCount = recent.size
                        val mockNeeded = 5 - matchCount

                        val matches = if (mockNeeded > 0) {
                            val mockMatches = generateMockMatches(teamName, mockNeeded)
                            recent + mockMatches
                        } else {
                            recent
                        }

                        showMatches(matches)
                    } else {
                        showError("응답 오류: ${response.code()}")
                        showMatches(generateMockMatches(teamName, 5))
                    }
                }

                override fun onFailure(call: Call<SportsResponse>, t: Throwable) {
                    showError("API 호출 실패: ${t.message}")
                    showMatches(generateMockMatches(teamName, 5))
                }
            })
    }

    private fun showMatches(matches: List<Match>) {
        val inflater = LayoutInflater.from(this)
        for (match in matches) {
            val itemView = inflater.inflate(R.layout.recent_result_item, resultContainer, false)

            val score = "${match.strHomeTeam} ${match.intHomeScore ?: "-"} - ${match.intAwayScore ?: "-"} ${match.strAwayTeam}"

            val rawTime = match.strTime ?: ""
            val shortTime = if (rawTime.contains(":")) rawTime.substringBeforeLast(":") else rawTime
            val time = "${match.dateEvent ?: ""} ${match.strTime?.take(5) ?: ""}"

            itemView.findViewById<TextView>(R.id.textMatchScore).text = score
            itemView.findViewById<TextView>(R.id.textMatchDate).text = time

            resultContainer.addView(itemView)
        }
    }

    // 실제 API 결과가 부족하거나 비시즌일 경우, 아래 목업 데이터를 활용
    // -> 시즌 종료 후 일정이 없을 경우 최근 경기를 대체로 보여줌
    private fun generateMockMatches(teamName: String, count: Int): List<Match> {
        val mockTemplate = listOf(
            Triple("Chelsea", "2 - 1", "2024-08-15 21:00"),
            Triple("Manchester United", "0 - 0", "2024-05-10 19:30"),
            Triple("Tottenham", "3 - 1", "2024-05-05 20:00"),
            Triple("Aston Villa", "4 - 0", "2024-04-28 18:00"),
            Triple("Man City", "2 - 2", "2024-04-28 17:30")
        )

        return mockTemplate.take(count).mapIndexed { index, (opponent, scoreStr, dateTime) ->
            val (homeScore, awayScore) = scoreStr.split("-").map { it.trim().toInt() }
            val (date, time) = dateTime.split(" ")

            Match(
                strHomeTeam = if (index % 2 == 0) teamName else opponent,
                strAwayTeam = if (index % 2 == 0) opponent else teamName,
                intHomeScore = if (index % 2 == 0) homeScore else awayScore,
                intAwayScore = if (index % 2 == 0) awayScore else homeScore,
                dateEvent = date,
                strEvent = null,
                strTime = time
            )
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
