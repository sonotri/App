package com.example.guru2

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility.getKeyHash
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {

    private lateinit var idEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var joinTextView: TextView
    private lateinit var kakaoLoginButton: Button
    private lateinit var dbHelper: UserDBHelper //DB 선언 추가
    private lateinit var findAccountTextView: TextView // 추가
    private lateinit var sharedPref: SharedPreferences // 현재 로그인한 사용자 정보

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)

        // 해시 키 확인용 로그 출력
        Log.d("KeyHash", getKeyHash(this))

        idEditText = findViewById(R.id.editTextId)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        joinTextView = findViewById(R.id.textViewSignup) // 회원가입 TextView
        kakaoLoginButton = findViewById(R.id.buttonKakaoLogin)
        findAccountTextView = findViewById(R.id.textFindAccount)

        dbHelper = UserDBHelper(this) // DB 연결
        sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)

        // 로그인 버튼 클릭 시
        loginButton.setOnClickListener {
            val id = idEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            when {
                id.isEmpty() || password.isEmpty() -> {
                    Toast.makeText(this, "아이디와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                !isValidPassword(password) -> {
                    Toast.makeText(this, "비밀번호는 8자 이상, 특수문자를 포함해야 합니다", Toast.LENGTH_SHORT).show()
                }

                // 테스트용 아이디
                id == "testuser" && password == "Test@1234" -> {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                else -> {
                    val db = dbHelper.readableDatabase // DB에서 회원정보 조회
                    val cursor = db.rawQuery(
                        "SELECT * FROM user WHERE id = ? AND password = ?",
                        arrayOf(id, password)
                    )

                    if (cursor.moveToFirst()) {
                        val nickname = cursor.getString(cursor.getColumnIndexOrThrow("nickname"))
                        val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))

                        saveUserToSQLiteAndPrefs(id, nickname, email, password, "local")

                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "아이디 또는 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
                    }
                    cursor.close()
                    db.close()
                }
            }
        }

        // 카카오 로그인 버튼 클릭 시
        kakaoLoginButton.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null && error is ClientError && error.reason == ClientErrorCause.Cancelled) return@loginWithKakaoTalk
                    if (token != null) handleKakaoLogin()
                    else UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoCallback)
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoCallback)
            }
        }

        // 회원가입 버튼 클릭 시
        joinTextView.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }

        findAccountTextView.setOnClickListener {
            val intent = Intent(this, FindAccountActivity::class.java)
            startActivity(intent)
        }


    }

    // 카카오 로그인 처리 콜백
    private val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e("KakaoLogin", "카카오 로그인 실패", error)
        } else if (token != null) {
            handleKakaoLogin()
        }
    }

    // 카카오 사용자 정보 받아 저장
    private fun handleKakaoLogin() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
            } else if (user != null) {
                val kakaoId = user.id.toString()
                val email = user.kakaoAccount?.email ?: "null@null.com"
                // DB 조회
                val dbHelper = UserDBHelper(this)
                val db = dbHelper.readableDatabase
                val cursor = db.rawQuery("SELECT * FROM user WHERE id = ?", arrayOf(kakaoId))

                if (cursor.moveToFirst()) {
                    // 이미 가입된 사용자 → SharedPreferences 저장하고 MainActivity로
                    val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
                    sharedPref.edit().apply {
                        putString("loggedInUserId", kakaoId)
                        putString("loginType", "kakao")
                        apply()
                    }

                    cursor.close()
                    db.close()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // 최초 로그인 사용자 → 닉네임 설정 화면으로 이동
                    cursor.close()
                    db.close()

                    val intent = Intent(this, SetNicknameActivity::class.java).apply {
                        putExtra("kakaoId", kakaoId)
                        putExtra("email", email)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun saveUserToSQLiteAndPrefs(id: String, nickname: String, email: String, password: String, loginType: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", id)
            put("nickname", nickname)
            put("email", email)
            put("password", password)
            put("login_type", loginType)
        }
        db.insertWithOnConflict("user", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        db.close()

        sharedPref.edit()
            .putString("loggedInUserId", id)
            .putString("loginType", loginType)
            .apply()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[!@#\$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#\$%^&*(),.?\":{}|<>]{8,}$")
        return passwordRegex.matches(password)
    }
}