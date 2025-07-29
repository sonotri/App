package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TeamDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_detail)

        // 팀 이름 받아오기
        val teamName = intent.getStringExtra("teamName") ?: "Unknown Team"

        // 팀 이름 표시
        val textTeamName = findViewById<TextView>(R.id.textTeamName)
        textTeamName.text = teamName

        // 각 버튼(또는 텍스트뷰) 클릭 이벤트 설정
        val upcomingText = findViewById<TextView>(R.id.upcomingMatchesTextView)
        val recentText = findViewById<TextView>(R.id.recentResultsTextView)
        val playersText = findViewById<TextView>(R.id.playersTextView)

        // Upcoming Matches 이동
        upcomingText.setOnClickListener {
            val intent = Intent(this, UpcomingMatchActivity::class.java)
            intent.putExtra("teamName", teamName)
            startActivity(intent)
        }

        // Recent Results 이동
        recentText.setOnClickListener {
            val intent = Intent(this, RecentResultActivity::class.java)
            intent.putExtra("teamName", teamName)
            startActivity(intent)
        }

        // Players 이동
        playersText.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java)
            intent.putExtra("teamName", teamName)
            startActivity(intent)
        }
    }
}
