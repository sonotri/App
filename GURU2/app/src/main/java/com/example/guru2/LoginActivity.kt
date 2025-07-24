package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var idEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var joinTextView: TextView // 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)

        idEditText = findViewById(R.id.editTextId)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        joinTextView = findViewById(R.id.textViewSignup) // 회원가입 TextView

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

                id == "testuser" && password == "Test@1234" -> {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                else -> {
                    Toast.makeText(this, "아이디 또는 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ✅ 회원가입 클릭 시 JoinActivity로 이동
        joinTextView.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[!@#\$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#\$%^&*(),.?\":{}|<>]{8,}$")
        return passwordRegex.matches(password)
    }
}
