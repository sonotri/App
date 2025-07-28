// TeamDetailActivity.kt
package com.example.guru2

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TeamDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_detail)

        //팀 이름 TextView 찾기
        val textTeamName = findViewById<TextView>(R.id.textTeamName)

        // TeamListActivity으로부터 팀명 가져오기
        val teamName = intent.getStringExtra("teamName") ?: "Unknown Team"

        // 텍스트 뷰에 팀명 설정
        textTeamName.text = teamName

        // API 연동 필요. 일정, 경기 기록, 선수.....
    }
}


