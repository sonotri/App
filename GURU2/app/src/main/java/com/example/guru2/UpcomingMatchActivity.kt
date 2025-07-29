package com.example.guru2

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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

        titleText.text = "$teamName - Upcoming Matches"

        fetchUpcomingMatches()
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

                    if (filteredMatches.isNotEmpty()) {
                        showMatches(filteredMatches)
                    } else {
                        showMockData()
                    }
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
            dateView.text = "${match.dateEvent} ${match.strTime}"

            scheduleContainer.addView(itemView)
        }
    }

    private fun showMockData() {
        val notice = TextView(this).apply {
            text = "(비시즌 기간으로, 아래는 예시 데이터입니다)"
            textSize = 14f
            setPadding(0, 24, 0, 24)
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        scheduleContainer.addView(notice)

        val mockMatches = listOf(
            "Liverpool vs Manchester United" to "2025-08-10 20:00",
            "Chelsea vs Liverpool" to "2025-08-15 19:00",
            "Liverpool vs Arsenal" to "2025-08-22 18:00",
            "Manchester City vs Liverpool" to "2025-08-28 21:00",
            "Liverpool vs Tottenham" to "2025-09-01 17:30"
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
