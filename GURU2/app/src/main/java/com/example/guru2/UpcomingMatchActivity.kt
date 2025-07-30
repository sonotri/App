package com.example.guru2

import android.content.Intent
import android.icu.text.SimpleDateFormat
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
import java.util.*

class UpcomingMatchActivity : AppCompatActivity() {

    private lateinit var teamName: String
    private lateinit var titleText: TextView
    private lateinit var scheduleContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upcoming_match)

        teamName = intent.getStringExtra("teamName") ?: "Unknown Team"
        titleText = findViewById(R.id.upcomingMatchTitle)
        scheduleContainer = findViewById(R.id.scheduleContainer)

        titleText.text = "$teamName - 다가오는 경기 일정"

        fetchUpcomingMatches()

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

    private fun fetchUpcomingMatches() {
        val call = RetrofitClient.api.getUpcomingEvents()

        call.enqueue(object : Callback<SportsResponse> {
            override fun onResponse(call: Call<SportsResponse>, response: Response<SportsResponse>) {
                if (response.isSuccessful) {
                    val allMatches = response.body()?.events ?: emptyList()
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val today = formatter.parse(formatter.format(Date()))

                    val filteredMatches = allMatches
                        .filter { match ->
                            val matchDate = try {
                                formatter.parse(match.dateEvent ?: "")
                            } catch (e: Exception) {
                                null
                            }
                            (match.strHomeTeam?.contains(teamName, ignoreCase = true) == true ||
                                    match.strAwayTeam?.contains(teamName, ignoreCase = true) == true) &&
                                    (matchDate != null && matchDate.after(today))
                        }
                        .sortedBy { it.dateEvent }
                        .take(5)

                    val finalMatchList = filteredMatches.toMutableList()

                    // 실제 API 결과가 부족하거나 비시즌일 경우, 아래 목업 데이터를 활용
                    // -> 시즌 종료 후 일정이 없을 경우 최근 경기를 대체로 보여줌
                    if (finalMatchList.size < 5) {
                        val needed = 5 - finalMatchList.size
                        val mockMatches = listOf(
                            "$teamName vs Chelsea" to "2025-08-10 20:00",
                            "$teamName vs Manchester United" to "2025-08-15 19:00",
                            "Tottenham vs $teamName" to "2025-08-22 18:00",
                            "$teamName vs Aston Villa" to "2025-08-28 21:00",
                            "Manchester City vs $teamName" to "2025-09-01 17:30"
                        )

                        for ((matchText, dateText) in mockMatches.take(needed)) {
                            val parts = matchText.split(" vs ")
                            finalMatchList.add(
                                Match(
                                    dateEvent = dateText.split(" ")[0],
                                    strEvent = matchText,
                                    strHomeTeam = parts[0],
                                    strAwayTeam = parts[1],
                                    intHomeScore = null,
                                    intAwayScore = null,
                                    strTime = dateText.split(" ").getOrNull(1)
                                )
                            )
                        }
                    }

                    showMatches(finalMatchList)
                } else {
                    showError("응답 오류: ${response.code()}")
                    showMockData()
                }
            }

            override fun onFailure(call: Call<SportsResponse>, t: Throwable) {
                showError("API 호출 실패: ${t.message}")
                Log.e("UpcomingMatchActivity", "API 호출 실패", t)
                showMockData()
            }
        })
    }


    private fun showMatches(matches: List<Match>) {
        for (match in matches) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.recent_result_item, scheduleContainer, false)

            val scoreView = itemView.findViewById<TextView>(R.id.textMatchScore)
            val dateView = itemView.findViewById<TextView>(R.id.textMatchDate)

            scoreView.text = "${match.strHomeTeam} vs ${match.strAwayTeam}"

            val cleanTime = match.strTime?.take(5) ?: ""
            dateView.text = "${match.dateEvent} $cleanTime"

            scheduleContainer.addView(itemView)
        }
    }

    // 목업 데이터
    private fun showMockData() {
        val mockMatches = listOf(
            "$teamName vs Chelsea" to "2025-09-10 20:00",
            "$teamName vs Manchester United" to "2025-09-15 19:00",
            "Tottenham vs $teamName" to "2025-09-22 18:00",
            "$teamName vs Aston Villa" to "2025-10-01 21:00",
            "Manchester City vs $teamName" to "2025-10-11 17:30"
        )

        for ((matchText, dateText) in mockMatches) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.recent_result_item, scheduleContainer, false)

            val scoreView = itemView.findViewById<TextView>(R.id.textMatchScore)
            val dateView = itemView.findViewById<TextView>(R.id.textMatchDate)

            scoreView.text = matchText
            dateView.text = dateText

            scheduleContainer.addView(itemView)
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
