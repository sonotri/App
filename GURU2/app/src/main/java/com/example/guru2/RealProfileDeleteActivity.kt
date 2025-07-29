package com.example.guru2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RealProfileDeleteActivity : AppCompatActivity() {

    private lateinit var buttonWithdraw: Button
    private lateinit var editPassword: EditText
    private lateinit var dbHelper: UserDBHelper

    // 툴바
    private lateinit var buttonSchedule: ImageButton
    private lateinit var buttonPlayer: ImageButton
    private lateinit var buttonHome: ImageButton
    private lateinit var buttonLocation: ImageButton
    private lateinit var buttonProfile: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_profile_real)

        editPassword = findViewById(R.id.editTextCurrentPassword)
        buttonWithdraw = findViewById(R.id.buttonWithdraw)
        dbHelper = UserDBHelper(this)

        // 툴바 버튼 연동
        buttonSchedule = findViewById(R.id.btn_schedule)
        buttonPlayer = findViewById(R.id.btn_player)
        buttonHome = findViewById(R.id.btn_home)
        buttonLocation = findViewById(R.id.btn_location)
        buttonProfile = findViewById(R.id.btn_profile)

        // 로그인 정보 가져오기
        val sharedPref = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("loggedInUserId", null)
        val loginType = sharedPref.getString("loginType", null)

        // 카카오 로그인 사용자는 비밀번호 입력창 비활성화
        if (loginType == "kakao") {
            editPassword.isEnabled = false
            editPassword.hint = "카카오 계정은 비밀번호 입력 없음"
        }

        // 탈퇴 버튼 클릭
        buttonWithdraw.setOnClickListener {
            if (userId == null) {
                Toast.makeText(this, "로그인 정보가 없습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 카카오 로그인 사용자
            if (loginType == "kakao") {
                // 비밀번호 없이 바로 탈퇴
                val db = dbHelper.writableDatabase
                db.delete("user", "id = ?", arrayOf(userId))
                db.close()

                sharedPref.edit().clear().apply()

                Toast.makeText(this, "회원 탈퇴가 완료되었습니다", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return@setOnClickListener
            }

            // 일반 로그인 사용자
            val inputPassword = editPassword.text?.toString() ?: ""
            if (inputPassword.isEmpty()) {
                Toast.makeText(this, "패스워드를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM user WHERE id = ? AND password = ?", arrayOf(userId, inputPassword))

            if (cursor.moveToFirst()) {
                cursor.close()
                val writeDb = dbHelper.writableDatabase
                writeDb.delete("user", "id = ?", arrayOf(userId))
                dbHelper.close()

                sharedPref.edit().clear().apply()

                Toast.makeText(this, "회원 탈퇴가 완료되었습니다", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else {
                cursor.close()
                Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            }
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
