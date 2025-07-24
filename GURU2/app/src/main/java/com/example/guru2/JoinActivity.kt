package com.example.guru2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class JoinActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var nicknameEditText: EditText
    private lateinit var checkNicknameButton: Button
    private lateinit var idEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var joinButton: Button

    private var isNicknameChecked = false // 중복 확인 여부

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        emailEditText = findViewById(R.id.editTextEmail)
        nicknameEditText = findViewById(R.id.editTextNickname)
        checkNicknameButton = findViewById(R.id.buttonCheckNickname)
        idEditText = findViewById(R.id.editTextId)
        passwordEditText = findViewById(R.id.editTextPassword)
        confirmPasswordEditText = findViewById(R.id.editTextPasswordConfirm)
        joinButton = findViewById(R.id.buttonJoin)

        nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isNicknameChecked = false
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 닉네임 중복 확인 클릭 시
        checkNicknameButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString().trim()
            if (nickname.isEmpty()) {
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 예시: 중복 체크 로직은 서버 API로 교체 가능
            if (nickname == "admin" || nickname == "test") {
                Toast.makeText(this, "이미 사용 중인 닉네임입니다", Toast.LENGTH_SHORT).show()
                isNicknameChecked = false
            } else {
                Toast.makeText(this, "사용 가능한 닉네임입니다", Toast.LENGTH_SHORT).show()
                isNicknameChecked = true
            }
        }

        // 회원가입 버튼 클릭 시
        joinButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val nickname = nicknameEditText.text.toString().trim()
            val userId = idEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // 입력값 유효성 검사
            when {
                email.isEmpty() || nickname.isEmpty() || userId.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ->
                    Toast.makeText(this, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show()

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    Toast.makeText(this, "유효한 이메일을 입력해주세요", Toast.LENGTH_SHORT).show()

                !isNicknameChecked ->
                    Toast.makeText(this, "닉네임 중복 확인을 해주세요", Toast.LENGTH_SHORT).show()

                password != confirmPassword ->
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()

                else -> {
                    // TODO: 서버에 회원가입 요청 (ex. Retrofit, Firebase)
                    Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
