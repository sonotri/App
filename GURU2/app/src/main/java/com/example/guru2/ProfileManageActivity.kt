package com.example.guru2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.user.UserApiClient

class ProfileManageActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editNickname: EditText
    private lateinit var editUserId: EditText
    private lateinit var editPassword: EditText
    private lateinit var editCurrentPassword: EditText
    private lateinit var buttonUpdate: Button

    private lateinit var dbHelper: UserDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_management_profile)

        editEmail = findViewById(R.id.editTextEmail)
        editNickname = findViewById(R.id.editTextNickname)
        editUserId = findViewById(R.id.editTextUserId)
        editPassword = findViewById(R.id.editTextPassword)
        editCurrentPassword = findViewById(R.id.editTextCurrentPassword)
        buttonUpdate = findViewById(R.id.buttonUpdate)

        dbHelper = UserDBHelper(this)

        // 로그인된 사용자 ID 가져오기
        val sharedPref = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val loginType = sharedPref.getString("loginType", null)
        val userId = sharedPref.getString("loggedInUserId", null)

        if (userId == null) {
            Toast.makeText(this, "로그인 정보가 없습니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (loginType == "local") {
            // 기존 사용자 정보 불러오기
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM user WHERE id = ?", arrayOf(userId))

            if (cursor.moveToFirst()) {
                editEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")))
                editNickname.setText(cursor.getString(cursor.getColumnIndexOrThrow("nickname")))
                editUserId.setText(cursor.getString(cursor.getColumnIndexOrThrow("id")))
                editPassword.setText(cursor.getString(cursor.getColumnIndexOrThrow("password")))
            }
            cursor.close()
            db.close()
        } else if (loginType == "kakao") {
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Toast.makeText(this, "카카오 정보 조회 실패", Toast.LENGTH_SHORT).show()
                    finish()
                } else if (user != null) {
                    // 기존 사용자 정보 불러오기
                    val db = dbHelper.readableDatabase
                    val cursor = db.rawQuery("SELECT * FROM user WHERE id = ?", arrayOf(userId))

                    if (cursor.moveToFirst()) {
                        editEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")))
                        editNickname.setText(cursor.getString(cursor.getColumnIndexOrThrow("nickname")))
                        editUserId.setText(cursor.getString(cursor.getColumnIndexOrThrow("id")))
                    }
                    cursor.close()
                    db.close()

                    editCurrentPassword.isEnabled = false
                    editPassword.isEnabled = false
                    editCurrentPassword.setText("비밀번호 없음")
                    editPassword.setText("비밀번호 없음")
                }
            }
        }

        // 변경 버튼 클릭
        buttonUpdate.setOnClickListener {
            val newEmail = editEmail.text.toString()
            val newNickname = editNickname.text.toString()

            // 유효성 검사
            if (!isValidNickname(newNickname)) {
                Toast.makeText(this, "닉네임은 3자 이상 10자 이하의 한글로 구성되어야 합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (loginType == "local") {
                val currentPw = editCurrentPassword.text.toString()
                val newPw = editPassword.text.toString()

                if (!isValidPassword(newPw)) {
                    Toast.makeText(this, "비밀번호는 8자 이상, 특수문자를 포함해야 합니다", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val dbCheck = dbHelper.readableDatabase
                val checkCursor = dbCheck.rawQuery(
                    "SELECT * FROM user WHERE id = ? AND password = ?",
                    arrayOf(userId, currentPw)
                )

                if (!checkCursor.moveToFirst()) {
                    Toast.makeText(this, "현재 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                    checkCursor.close()
                    return@setOnClickListener
                }
                checkCursor.close()

                val dbUpdate = dbHelper.writableDatabase
                dbUpdate.execSQL(
                    "UPDATE user SET email = ?, nickname = ?, password = ? WHERE id = ?",
                    arrayOf(newEmail, newNickname, newPw, userId)
                )
                dbUpdate.close()

            } else if (loginType == "kakao") {
                val dbUpdate = dbHelper.writableDatabase
                dbUpdate.execSQL(
                    "UPDATE user SET email = ?, nickname = ? WHERE id = ?",
                    arrayOf(newEmail, newNickname, userId)
                )
                dbUpdate.close()
            }

            Toast.makeText(this, "회원정보가 수정되었습니다", Toast.LENGTH_SHORT).show()
            finish()
        }

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


    private fun isValidNickname(nickname: String): Boolean {
        val nicknameRegex = Regex("^[가-힣]{3,10}$")
        return nicknameRegex.matches(nickname)
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*[!@#\$%^&*()_+=\\-{}|:;\"'<>,.?/]).{8,}$")
        return passwordRegex.matches(password)
    }
}

