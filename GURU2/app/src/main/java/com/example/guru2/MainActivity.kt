package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 시스템 UI 여백 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ⬇ 툴바 버튼 연결
        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        val btnSchedule = findViewById<ImageButton>(R.id.btn_schedule)
        val btnPlayer = findViewById<ImageButton>(R.id.btn_player)
        val btnLocation = findViewById<ImageButton>(R.id.btn_location)
        val btnProfile = findViewById<ImageButton>(R.id.btn_profile)

        // ⬇ 버튼 클릭 이벤트 처리
        btnHome.setOnClickListener {
            // 현재 MainActivity 이므로 새로 열 필요 없음
        }

        btnSchedule.setOnClickListener {
            val intent = Intent(this, ScheduleActivity::class.java)
            startActivity(intent)
        }

        btnPlayer.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }

        btnLocation.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }
}
