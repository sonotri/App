package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var matchRecyclerView: RecyclerView
    private lateinit var upcomingRecyclerView: RecyclerView
    private lateinit var noMatchesTextView: TextView
    private lateinit var upcomingTitleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        matchRecyclerView = findViewById(R.id.recyclerViewMatches)
        upcomingRecyclerView = findViewById(R.id.recyclerViewUpcomingMatches)
        noMatchesTextView = findViewById(R.id.text_no_matches)
        upcomingTitleTextView = findViewById(R.id.text_upcoming_title)

        matchRecyclerView.layoutManager = LinearLayoutManager(this)
        upcomingRecyclerView.layoutManager = LinearLayoutManager(this)

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // 오늘 경기 요청
        RetrofitClient.api.getTodayEvents(today).enqueue(object : Callback<SportsResponse> {
            override fun onResponse(call: Call<SportsResponse>, response: Response<SportsResponse>) {
                val matches = response.body()?.events ?: emptyList()

                if (matches.isEmpty()) {
                    noMatchesTextView.text = "$today 오늘은 경기 일정이 없습니다"
                    noMatchesTextView.visibility = View.VISIBLE
                    loadUpcomingMatches() // 다가오는 경기 불러오기
                } else {
                    matchRecyclerView.adapter = MatchAdapter(matches)
                    noMatchesTextView.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<SportsResponse>, t: Throwable) {
                Log.e("API_FAIL", "에러: ${t.message}")
                noMatchesTextView.text = "경기 정보를 불러오지 못했습니다."
                noMatchesTextView.visibility = View.VISIBLE
            }
        })

        // 시스템 UI 여백 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
            startActivity(Intent(this, LocationActivity::class.java))
        }
        btnLocation.setOnClickListener {
            startActivity(Intent(this, LocationActivity::class.java))
        }
        btnProfile.setOnClickListener {
            startActivity(Intent(this, MypageActivity::class.java))
        }
    }

    // 다가오는 경기 일정 로드 함수
    private fun loadUpcomingMatches() {
        RetrofitClient.api.getUpcomingEvents().enqueue(object : Callback<SportsResponse> {
            override fun onResponse(call: Call<SportsResponse>, response: Response<SportsResponse>) {
                val allUpcoming = response.body()?.events ?: return

                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = formatter.parse(formatter.format(Date()))

                val closest = allUpcoming
                    .filter {
                        try {
                            val eventDate = formatter.parse(it.dateEvent ?: "9999-12-31")
                            eventDate != null && eventDate.after(today)
                        } catch (e: Exception) {
                            false
                        }
                    }
                    .sortedBy {
                        formatter.parse(it.dateEvent ?: "9999-12-31")
                    }
                    .take(2)

                if (closest.isNotEmpty()) {
                    upcomingTitleTextView.visibility = View.VISIBLE
                    upcomingRecyclerView.visibility = View.VISIBLE
                    upcomingRecyclerView.adapter = MatchAdapter(closest)
                }
            }

            override fun onFailure(call: Call<SportsResponse>, t: Throwable) {
                Log.e("UPCOMING_FAIL", "다가오는 경기 로드 실패: ${t.message}")
            }
        })
    }
}
