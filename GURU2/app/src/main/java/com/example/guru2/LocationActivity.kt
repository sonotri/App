package com.example.guru2

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationActivity : AppCompatActivity() {

    private lateinit var teamInput: EditText
    private lateinit var btnSearch: Button
    private lateinit var stadiumImage: ImageView
    private lateinit var textInfo: TextView
    private lateinit var teamLogo: ImageView
    private lateinit var stadiumThumbLocal: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        teamInput = findViewById(R.id.editTextTeam)
        btnSearch = findViewById(R.id.btnSearch)
        stadiumImage = findViewById(R.id.stadiumImage)
        stadiumThumbLocal = findViewById(R.id.stadiumThumbLocal)
        textInfo = findViewById(R.id.textInfo)
        teamLogo = findViewById(R.id.teamLogo)

        btnSearch.setOnClickListener {
            val teamName = teamInput.text.toString().trim()
            if (teamName.isNotEmpty()) {
                searchTeamInfo(teamName)
            } else {
                Toast.makeText(this, "팀 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchTeamInfo(teamName: String) {
        RetrofitClient.api.searchTeam(teamName).enqueue(object : Callback<TeamResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<TeamResponse>, response: Response<TeamResponse>) {
                if (!response.isSuccessful || response.body()?.teams.isNullOrEmpty()) {
                    Toast.makeText(this@LocationActivity, "팀 정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
                    return
                }

                val team = response.body()?.teams?.first() ?: return
                val locationText = team.strStadiumLocation ?: getStadiumLocation(team.strTeam ?: "")

                textInfo.text = """
                    팀명: ${team.strTeam}
                    구장: ${team.strStadium}
                    위치: $locationText
                    수용 인원: ${team.intStadiumCapacity ?: "정보 없음"}
                """.trimIndent()

                // 팀 로고 (drawable)
                val logoName = team.strTeam?.let { getTeamLogoName(it.lowercase()) }
                val logoResId = resources.getIdentifier(logoName, "drawable", packageName)
                if (logoResId != 0) {
                    teamLogo.setImageResource(logoResId)
                } else {
                    teamLogo.setImageResource(R.drawable.round_border)
                }

                // 구장 이미지 (drawable)
                val stadiumImgName = team.strTeam?.let { getStadiumImageName(it.lowercase()) }
                val stadiumResId = resources.getIdentifier(stadiumImgName, "drawable", packageName)
                if (stadiumResId != 0) {
                    stadiumThumbLocal.setImageResource(stadiumResId)
                } else {
                    stadiumThumbLocal.setImageResource(R.drawable.round_border)
                }
            }

            override fun onFailure(call: Call<TeamResponse>, t: Throwable) {
                Toast.makeText(this@LocationActivity, "API 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getTeamLogoName(team: String): String {
        return when (team) {
            "arsenal" -> "logo_arsenal"
            "aston villa" -> "logo_aston_villa"
            "bournemouth" -> "logo_bournemouth"
            "brentford" -> "logo_brentford"
            "brighton" -> "logo_brighton"
            "burnley" -> "logo_burnley"
            "chelsea" -> "logo_chelsea"
            "crystal palace" -> "logo_crystal_palace"
            "everton" -> "logo_everton"
            "fulham" -> "logo_fulham"
            "ipswich" -> "logo_ipswich"
            "leeds" -> "logo_leeds"
            "liverpool" -> "logo_liverpool"
            "manchester city" -> "logo_manchester_city"
            "manchester united" -> "logo_manchester_united"
            "newcastle" -> "logo_newcastle"
            "nottingham" -> "logo_nottingham"
            "tottenham" -> "logo_tottenham"
            "west ham" -> "logo_west_ham"
            "wolves" -> "logo_wolves"
            else -> "정보 없음"
        }
    }

    private fun getStadiumImageName(team: String): String {
        return when (team) {
            "arsenal" -> "st_arsenal"
            "aston villa" -> "st_aston_villa"
            "bournemouth" -> "st_bournemouth"
            "brentford" -> "st_brentford"
            "brighton" -> "st_brighton"
            "burnley" -> "st_burnley"
            "chelsea" -> "st_chelsea"
            "crystal palace" -> "st_crystal_palace"
            "everton" -> "st_everton"
            "fulham" -> "st_fulham"
            "ipswich" -> "st_ipswich"
            "leeds" -> "st_leeds"
            "liverpool" -> "st_liverpool"
            "manchester city" -> "st_manchester_city"
            "manchester united" -> "st_manchester_united"
            "newcastle" -> "st_newcastle"
            "nottingham" -> "st_nottingham"
            "tottenham" -> "st_tottenham"
            "west ham" -> "st_west_ham"
            "wolves" -> "st_wolves"
            else -> "정보 없음"
        }

    }

    private fun getStadiumLocation(team: String): String {
        return when (team.lowercase()) {
            "arsenal" -> "영국 런던 이즐링턴"
            "aston villa" -> "영국 버밍엄"
            "bournemouth" -> "영국 본머스"
            "brentford" -> "영국 런던"
            "brighton" -> "영국 브라이튼시"
            "burnley" -> "영국 랭커셔 주 번리"
            "chelsea" -> "영국 런던"
            "crystal palace" -> "영국 런던 크로이던 구"
            "everton" -> "영국 리버풀 리전트 로드"
            "fulham" -> "영국 런던"
            "ipswich" -> "영국 입스위치"
            "leeds" -> "영국 웨스트요크셔 주 리즈시 브레스턴"
            "liverpool" -> "영국 리버풀 머지사이드주 리버풀"
            "manchester city" -> "영국 맨체스터"
            "manchester united" -> "영국 맨체스터"
            "newcastle" -> "영국 타인 위어 주 뉴캐슬어폰타인"
            "nottingham" -> "영국 노팅엄 시 "
            "tottenham" -> "영국 런던"
            "west ham" -> "영국 런던 스트랫포드"
            "wolves" -> "영국 울버햄튼 시 워털루 로드"
            else -> "정보 없음"
        }
    }

}
