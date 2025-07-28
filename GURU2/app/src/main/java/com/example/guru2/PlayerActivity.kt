package com.example.guru2

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class PlayerActivity : AppCompatActivity() {

    data class Team(val name: String, val logoRes: Int)

    private val teamList = listOf(
        Team("Arsenal", R.drawable.logo_arsenal),
        Team("Aston Villa", R.drawable.logo_aston_villa),
        Team("Bournemouth", R.drawable.logo_bournemouth),
        Team("Brentford", R.drawable.logo_brentford),
        Team("Brighton", R.drawable.logo_brighton),
        Team("Burnley", R.drawable.logo_burnley),
        Team("Chelsea", R.drawable.logo_chelsea),
        Team("Crystal Palace", R.drawable.logo_crystal_palace),
        Team("Everton", R.drawable.logo_everton),
        Team("Fulham", R.drawable.logo_fulham),
        Team("Leeds United", R.drawable.logo_leeds),
        Team("Liverpool", R.drawable.logo_liverpool),
        Team("Manchester City", R.drawable.logo_manchester_city),
        Team("Manchester United", R.drawable.logo_manchester_united),
        Team("Newcastle United", R.drawable.logo_newcastle),
        Team("Nottingham Forest", R.drawable.logo_nottingham),
        Team("Tottenham Hotspur", R.drawable.logo_tottenham),
        Team("West Ham United", R.drawable.logo_west_ham),
        Team("Wolverhampton", R.drawable.logo_wolves),
        Team("Ipswich Town", R.drawable.logo_ipswich)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val teamContainer = findViewById<LinearLayout>(R.id.teamContainer)

        for (team in teamList) {
            val box = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundResource(R.drawable.less_round_border)
                setPadding(24.dp, 16.dp, 24.dp, 16.dp)
                layoutParams = LinearLayout.LayoutParams(350.dp, 80.dp).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    bottomMargin = 20.dp
                }
                gravity = Gravity.CENTER_VERTICAL
                isClickable = true
                isFocusable = true

                setOnClickListener {
                    val intent = Intent(this@PlayerActivity, TeamDetailActivity::class.java)
                    intent.putExtra("teamName", team.name)
                    startActivity(intent)
                }

                addView(ImageView(this@PlayerActivity).apply {
                    setImageResource(team.logoRes)
                    layoutParams = LinearLayout.LayoutParams(48.dp, 48.dp)
                })

                addView(TextView(this@PlayerActivity).apply {
                    text = team.name
                    textSize = 20f
                    setTypeface(null, Typeface.BOLD)
                    setPadding(40.dp, 0, 0, 0)
                })
            }

            teamContainer.addView(box)
        }

        // 하단 메뉴 버튼 연결
        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        val btnSchedule = findViewById<ImageButton>(R.id.btn_schedule)
        val btnLocation = findViewById<ImageButton>(R.id.btn_location)
        val btnProfile = findViewById<ImageButton>(R.id.btn_profile)

        btnHome.setOnClickListener {}
        btnSchedule.setOnClickListener {
            startActivity(Intent(this, ScheduleActivity::class.java))
        }
        btnLocation.setOnClickListener {
            startActivity(Intent(this, LocationActivity::class.java))
        }
        btnProfile.setOnClickListener {
            startActivity(Intent(this, MypageActivity::class.java))
        }
    }

    // 확장 함수: dp → px 변환
    val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
}