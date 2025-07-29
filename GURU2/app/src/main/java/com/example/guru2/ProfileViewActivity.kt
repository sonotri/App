package com.example.guru2

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.user.UserApiClient

class ProfileViewActivity : AppCompatActivity() {

    private lateinit var nicknameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var userIdTextView: TextView
    private lateinit var passwordTextView: TextView

    private lateinit var dbHelper: UserDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)

        // TextView 연결
        nicknameTextView = findViewById(R.id.textViewNickname)
        emailTextView = findViewById(R.id.textViewEmail)
        userIdTextView = findViewById(R.id.textViewUserId)
        passwordTextView = findViewById(R.id.textViewPassword)

        // SharedPreferences에서 로그인된 사용자 ID 가져오기
        val sharedPref = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val loginType = sharedPref.getString("loginType", null)

        if (loginType == "local") {
            val userId = sharedPref.getString("loggedInUserId", null)
            if (userId == null) {
                Toast.makeText(this, "사용자 ID가 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            dbHelper = UserDBHelper(this)
            val db = dbHelper.readableDatabase

            val cursor = db.rawQuery("SELECT * FROM user WHERE id = ?", arrayOf(userId))

            if (cursor.moveToFirst()) {
                val nickname = cursor.getString(cursor.getColumnIndexOrThrow("nickname"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val password = cursor.getString(cursor.getColumnIndexOrThrow("password"))

                nicknameTextView.text = nickname
                emailTextView.text = email
                userIdTextView.text = userId
                passwordTextView.text = "*".repeat(password.length) // 마스킹 처리
            } else {
                Toast.makeText(this, "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }

            cursor.close()
            db.close()
        } else if (loginType == "kakao") {
            val userId = sharedPref.getString("loggedInUserId", null)
            if (userId == null) {
                Toast.makeText(this, "사용자 ID가 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            dbHelper = UserDBHelper(this)
            val db = dbHelper.readableDatabase

            val cursor = db.rawQuery("SELECT * FROM user WHERE id = ?", arrayOf(userId))

            if (cursor.moveToFirst()) {
                val nickname = cursor.getString(cursor.getColumnIndexOrThrow("nickname"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))

                nicknameTextView.text = nickname
                emailTextView.text = email
                userIdTextView.text = userId
                passwordTextView.text = "카카오 계정 로그인"
            } else {
                Toast.makeText(this, "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }

            cursor.close()
            db.close()
        } else {
            Toast.makeText(this, "로그인 정보 없음", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}
