package com.example.guru2

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
    }

    private fun fetchRecentResults() {
        RetrofitClient.api.getPastEvents().enqueue(object : Callback<SportsResponse> {
            override fun onResponse(call: Call<SportsResponse>, response: Response<SportsResponse>) {
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

                    if (recent.isNotEmpty()) {
                        showMatches(recent)
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
                showMockData()
            }
        })
    }

    private fun showMatches(matches: List<Match>) {
        val inflater = LayoutInflater.from(this)
        for (match in matches) {
            val itemView = inflater.inflate(R.layout.recent_result_item, resultContainer, false)
            val score = "${match.strHomeTeam} ${match.intHomeScore} - ${match.intAwayScore} ${match.strAwayTeam}"
            val time = "${match.dateEvent ?: ""} ${match.strTime ?: ""}"

            itemView.findViewById<TextView>(R.id.textMatchScore).text = score
            itemView.findViewById<TextView>(R.id.textMatchDate).text = time

            resultContainer.addView(itemView)
        }
    }

    private fun showMockData() {
        val notice = TextView(this).apply {
            text = "(비시즌 기간으로, 아래는 예시 데이터입니다)"
            textSize = 14f
            setPadding(0, 12, 0, 24)
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        resultContainer.addView(notice)

        val inflater = LayoutInflater.from(this)
        val mockResults = listOf(
            Pair("Liverpool 2 - 1 Chelsea", "2025-05-15 21:00"),
            Pair("Liverpool 0 - 0 Arsenal", "2025-05-10 19:30"),
            Pair("Tottenham 1 - 3 Liverpool", "2025-05-05 20:00"),
            Pair("Liverpool 4 - 0 Aston Villa", "2025-05-01 18:00"),
            Pair("Man City 2 - 2 Liverpool", "2025-04-28 17:30")
        )

        for ((score, date) in mockResults) {
            val itemView = inflater.inflate(R.layout.recent_result_item, resultContainer, false)
            itemView.findViewById<TextView>(R.id.textMatchScore).text = score
            itemView.findViewById<TextView>(R.id.textMatchDate).text = date
            resultContainer.addView(itemView)
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
