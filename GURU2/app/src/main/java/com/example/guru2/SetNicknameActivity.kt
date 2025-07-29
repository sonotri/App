package com.example.guru2

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SetNicknameActivity : AppCompatActivity() {

    private lateinit var editTextNickname: EditText
    private lateinit var buttonSubmit: Button
    private lateinit var sharedPref: SharedPreferences
    private lateinit var dbHelper: UserDBHelper
    private lateinit var buttonCheckDuplicate: Button
    private var isNicknameAvailable = false  // 중복확인 상태 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_nickname)

        editTextNickname = findViewById(R.id.editTextNickname)
        buttonSubmit = findViewById(R.id.buttonSubmitNickname)
        buttonCheckDuplicate = findViewById(R.id.buttonCheckDuplicate)

        sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        dbHelper = UserDBHelper(this)

        val kakaoId = intent.getStringExtra("kakaoId")
        val email = intent.getStringExtra("email") ?: "null@null.com"

        if (kakaoId == null) {
            Toast.makeText(this, "카카오 ID가 없습니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 닉네임 중복확인 버튼 클릭 후 텍스트 수정 시 중복확인 해제
        editTextNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isNicknameAvailable = false
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 닉네임 중복 확인 클릭 시
        buttonCheckDuplicate.setOnClickListener {
            val nickname = editTextNickname.text.toString().trim()

            if (nickname.isEmpty()) {
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!isValidNickname(nickname)) {
                Toast.makeText(this, "닉네임은 3자 이상 10자 이하의 한글로 구성되어야 합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 중복확인 쿼리
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM user WHERE nickname = ?", arrayOf(nickname)
            )

            if (cursor.moveToFirst()) {
                isNicknameAvailable = false
                Toast.makeText(this, "이미 사용 중인 닉네임입니다", Toast.LENGTH_SHORT).show()
            } else {
                isNicknameAvailable = true
                Toast.makeText(this, "사용 가능한 닉네임입니다", Toast.LENGTH_SHORT).show()
            }

            cursor.close()
            db.close()
        }

        buttonSubmit.setOnClickListener {
            val nickname = editTextNickname.text.toString().trim()

            if (nickname.isEmpty()) {
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isNicknameAvailable) {
                Toast.makeText(this, "닉네임 중복확인을 해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // DB에 사용자 정보 저장
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("id", kakaoId)
                put("nickname", nickname)
                put("email", email)
                put("password", "kakao")
                put("login_type", "kakao")
            }
            db.insertWithOnConflict("user", null, values, SQLiteDatabase.CONFLICT_IGNORE)
            db.close()

            // SharedPreferences 저장
            sharedPref.edit()
                .putString("loggedInUserId", kakaoId)
                .putString("loginType", "kakao")
                .apply()

            // 메인으로 이동
            Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun isValidNickname(nickname: String): Boolean {
        val idRegex = Regex("^[가-힣]{3,10}$")
        return idRegex.matches(nickname)
    }
}
