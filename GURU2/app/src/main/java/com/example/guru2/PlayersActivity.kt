package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlayersActivity : AppCompatActivity() {

    private lateinit var teamName: String
    private lateinit var titleText: TextView
    private lateinit var playersContainer: LinearLayout

    private val teamIdMap = mapOf(
        "Arsenal" to "133604",
        "Aston Villa" to "133626",
        "Bournemouth" to "133602",
        "Brentford" to "134777",
        "Brighton" to "134778",
        "Burnley" to "133610",
        "Chelsea" to "133610",
        "Crystal Palace" to "133619",
        "Everton" to "133615",
        "Fulham" to "133625",
        "Ipswich" to "133667",
        "Leeds United" to "133617",
        "Liverpool" to "133602",
        "Manchester City" to "133613",
        "Manchester United" to "133612",
        "Newcastle United" to "133610",
        "Nottingham Forest" to "134302",
        "Tottenham Hostspur" to "133616",
        "west ham United" to "133623",
        "wolverhampton" to "133632"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players)

        teamName = intent.getStringExtra("teamName") ?: "Unknown Team"
        titleText = findViewById(R.id.playersTitle)
        playersContainer = findViewById(R.id.playersContainer)

        titleText.text = "$teamName - 선수 목록"

        val teamId = teamIdMap[teamName] ?: return showError("지원되지 않는 팀입니다.")

        fetchPlayers(teamId)

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

    private fun fetchPlayers(teamId: String) {
        RetrofitClient.api.getPlayers(teamId).enqueue(object : Callback<PlayerResponse> {
            override fun onResponse(call: Call<PlayerResponse>, response: Response<PlayerResponse>) {
                if (response.isSuccessful) {
                    val players = response.body()?.player ?: emptyList()
                    for (player in players.take(10)) {
                        addPlayerView(player)
                    }
                } else {
                    showError("응답 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PlayerResponse>, t: Throwable) {
                showError("API 호출 실패: ${t.message}")
            }
        })
    }

    private fun addPlayerView(player: Player) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 16, 0, 16)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(150, 150)
        }

        val textView = TextView(this).apply {
            text = "${player.strPlayer} (${player.strPosition ?: "?"})"
            textSize = 16f
            setPadding(24, 0, 0, 0)
        }

        Glide.with(this).load(player.strCutout).into(imageView)

        layout.addView(imageView)
        layout.addView(textView)

        layout.setOnClickListener {
            val intent = Intent(this@PlayersActivity, PlayerInfoActivity::class.java).apply {
                putExtra("name", player.strPlayer)
                putExtra("position", player.strPosition ?: "Unknown")
                putExtra("nationality", player.strNationality ?: "Unknown")
                putExtra("birth", player.dateBorn ?: "Unknown")
                putExtra("image", player.strCutout)
            }
            startActivity(intent)
        }

        playersContainer.addView(layout)
    }


    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.e("PlayersActivity", message)
    }


}
