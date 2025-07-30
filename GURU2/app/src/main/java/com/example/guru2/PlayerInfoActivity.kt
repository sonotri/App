package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.guru2.PlayerActivity.Team

class PlayerInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_info)

        val name = intent.getStringExtra("name") ?: "이름 정보 없음"
        val position = intent.getStringExtra("position") ?: "정보 없음"
        val nationality = intent.getStringExtra("nationality") ?: "정보 없음"
        val birth = intent.getStringExtra("birth") ?: "정보 없음"
        val image = intent.getStringExtra("image")
        val teamName = intent.getStringExtra("teamName") ?: ""

        val nameView = findViewById<TextView>(R.id.playerName)
        val positionView = findViewById<TextView>(R.id.playerPosition)
        val nationalityView = findViewById<TextView>(R.id.playerNationality)
        val birthView = findViewById<TextView>(R.id.playerBirth)
        val imageView = findViewById<ImageView>(R.id.playerImage)
        val teamLogoView = findViewById<ImageView>(R.id.teamLogoImage)

        nameView.text = name
        positionView.text = "포지션: ${translatePosition(position)}"
        nationalityView.text = "국적: ${translateNationality(nationality)}"
        birthView.text = "생년월일: $birth"

        if (!image.isNullOrEmpty()) {
            Glide.with(this).load(image).into(imageView)
        }

        val teamLogoRes = R.drawable.ic_logo
        teamLogoView.setImageResource(teamLogoRes)

        // 하단 메뉴 버튼 연결
        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        val btnSchedule = findViewById<ImageButton>(R.id.btn_schedule)
        val btnPlayer = findViewById<ImageButton>(R.id.btn_player)
        val btnLocation = findViewById<ImageButton>(R.id.btn_location)
        val btnProfile = findViewById<ImageButton>(R.id.btn_profile)

        btnHome.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
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

    }

    private fun translatePosition(pos: String): String {
        return when (pos) {
            "Goalkeeper" -> "골키퍼"
            "Defender" -> "수비수"
            "Centre-Back" -> "센터백"
            "Right-Back" -> "오른쪽 수비수"
            "Left-Back" -> "왼쪽 수비수"
            "Midfielder" -> "미드필더"
            "Attacking Midfield" -> "공격형 미드필더"
            "Defensive Midfield" -> "수비형 미드필더"
            "Central Midfield" -> "중앙 미드필더"
            "Forward" -> "공격수"
            "Centre-Forward" -> "중앙 공격수"
            "Left Winger" -> "왼쪽 윙어"
            "Right Winger" -> "오른쪽 윙어"
            "Striker" -> "스트라이커"
            "Manager" -> "감독"
            else -> pos
        }
    }

    private fun translateNationality(nation: String): String {
        return when (nation) {
            "England" -> "잉글랜드"
            "France" -> "프랑스"
            "Spain" -> "스페인"
            "Germany" -> "독일"
            "Brazil" -> "브라질"
            "Argentina" -> "아르헨티나"
            "Portugal" -> "포르투갈"
            "Korea Republic" -> "대한민국"
            "Netherlands" -> "네덜란드"
            "Italy" -> "이탈리아"
            "Belgium" -> "벨기에"
            "Uruguay" -> "우루과이"
            else -> nation
        }
    }
}
