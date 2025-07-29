package com.example.guru2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.kakao.sdk.user.UserApiClient

class MypageActivity : AppCompatActivity() {

    private lateinit var cardProfileView: CardView
    private lateinit var cardProfileEdit: CardView
    private lateinit var cardWithdraw: CardView
    private lateinit var cardLogout: CardView

    // 툴바
    private lateinit var buttonSchedule: ImageButton
    private lateinit var buttonPlayer: ImageButton
    private lateinit var buttonHome: ImageButton
    private lateinit var buttonLocation: ImageButton
    private lateinit var buttonProfile: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        cardProfileView = findViewById(R.id.cardProfileView)
        cardProfileEdit = findViewById(R.id.cardProfileEdit)
        cardWithdraw = findViewById(R.id.cardWithdraw)
        cardLogout = findViewById(R.id.cardLogout)

        // 툴바 버튼 연동
        buttonSchedule = findViewById(R.id.btn_schedule)
        buttonPlayer = findViewById(R.id.btn_player)
        buttonHome = findViewById(R.id.btn_home)
        buttonLocation = findViewById(R.id.btn_location)
        buttonProfile = findViewById(R.id.btn_profile)

        // 로그인 정보 가져오기
        val sharedPref = getSharedPreferences("user_session", Context.MODE_PRIVATE)

        // 프로필 조회 클릭
        cardProfileView.setOnClickListener {
            Log.d("Mypage", "프로필 보기 버튼 클릭됨")
            val intent = Intent(this, ProfileViewActivity::class.java)
            startActivity(intent)
        }

        // 프로필 관리 클릭
        cardProfileEdit.setOnClickListener {
            val intent = Intent(this, ProfileManageActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 클릭
        cardLogout.setOnClickListener {
            val loginType = sharedPref.getString("loginType", null)

            // SharedPreferences 초기화
            sharedPref.edit().clear().apply()

            if (loginType == "kakao") {
                // 카카오 로그아웃 (세션 종료)
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Log.e("Kakao", "카카오 로그아웃 실패", error)
                    }
                }
            }

            Toast.makeText(this, "로그아웃 완료", Toast.LENGTH_SHORT).show()

            // 로그인 화면으로 이동
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // 탈퇴하기 클릭
        cardWithdraw.setOnClickListener {
            val intent = Intent(this, ProfileDeleteActivity::class.java)
            startActivity(intent)
        }


        // 툴바 버튼 리스너
        buttonSchedule.setOnClickListener {
            val intent = Intent(this, ScheduleActivity::class.java)
            startActivity(intent)
        }

        buttonPlayer.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }

        buttonHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        buttonLocation.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }

        buttonProfile.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }
}
