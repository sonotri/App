package com.example.guru2

import android.content.Intent
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

    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e("KakaoLogin", "카카오계정 로그인 실패", error)
        } else if (token != null) {
            Log.i("KakaoLogin", "카카오계정 로그인 성공 ${token.accessToken}")

            // 사용자 정보 요청
            UserApiClient.instance.me { user, error ->
                if (user != null) {
                    val nickname = user.kakaoAccount?.profile?.nickname
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("nickname", nickname)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)

        // 해시 키 확인용 로그 출력
        Log.d("KeyHash", getKeyHash(this) ?: "해시 키 추출 실패")

        idEditText = findViewById(R.id.editTextId)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        joinTextView = findViewById(R.id.textViewSignup) // 회원가입 TextView
        kakaoLoginButton = findViewById(R.id.buttonKakaoLogin)
        findAccountTextView = findViewById(R.id.textFindAccount)

        dbHelper = UserDBHelper(this) // DB 연결

        // 로그인 버튼 클릭 시
        loginButton.setOnClickListener {
            val id = idEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            when {
                id.isEmpty() || password.isEmpty() -> {
                    Toast.makeText(this, "아이디와 비밀번호를 모두 입력해주세요", Toast.LENGTH_SHORT).show()
                }

                !isValidPassword(password) -> {
                    Toast.makeText(this, "비밀번호는 8자 이상, 특수문자를 포함해야 합니다", Toast.LENGTH_SHORT).show()
                }

                // 테스트
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
                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "아이디 또는 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
                    }
                    cursor.close()
                }
            }
        }

//        kakaoLoginButton.setOnClickListener {
//            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
//                // 카카오톡 앱으로 로그인 시도
//                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
//                    if (error != null) {
//                        Log.e("KakaoLogin", "카카오톡 로그인 실패, 계정 로그인으로 대체", error)
//
//                        // 카카오계정 로그인으로 대체
//                        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
//                            handleKakaoLoginResult(token, error)
//                        }
//
//                    } else {
//                        handleKakaoLoginResult(token, error)
//                    }
//                }
//            } else {
//                // 카카오 계정으로 로그인 (웹뷰 방식)
//                UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
//                    handleKakaoLoginResult(token, error)
//                }
//            }
//        }

        // 카카오 로그인 버튼 클릭 시
        kakaoLoginButton.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e("KakaoLogin", "카카오톡 로그인 실패", error)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) return@loginWithKakaoTalk
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    } else if (token != null) {
                        Log.i("KakaoLogin", "카카오톡 로그인 성공 ${token.accessToken}")
                        callback(token, null)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
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

    // 카카오톡 로그인 콜백
//    private fun handleKakaoLoginResult(token: OAuthToken?, error: Throwable?) {
//        if (error != null) {
//            Log.e("KakaoLogin", "로그인 실패", error)
//            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
//        } else if (token != null) {
//            Log.d("KakaoLogin", "로그인 성공: ${token.accessToken}")
//
//            UserApiClient.instance.me { user, error ->
//                if (error != null) {
//                    Log.e("KakaoLogin", "사용자 정보 요청 실패", error)
//                } else if (user != null) {
////                    val id = user.id
////                    val email = user.kakaoAccount?.email
//                    val nickname = user.kakaoAccount?.profile?.nickname
//                    Log.d("KakaoLogin", "카카오 사용자 정보: $nickname")
//
//                    val intent = Intent(this, MainActivity::class.java)
//                    intent.putExtra("nickname", nickname)
//                    startActivity(intent)
//                    finish()
//                }
//            }
//        }
//    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[!@#\$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#\$%^&*(),.?\":{}|<>]{8,}$")
        return passwordRegex.matches(password)
    }
}