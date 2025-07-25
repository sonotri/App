package com.example.guru2

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class FindPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)

        val idEditText = findViewById<EditText>(R.id.editTextId)            // 아이디 입력
        val emailEditText = findViewById<EditText>(R.id.editTextPassword)   // 이메일 입력
        val resultTextView = findViewById<TextView>(R.id.textViewResult)    // 결과 출력 텍스트
        val clickButton = findViewById<Button>(R.id.buttonLogin)            // 버튼
        val backText = findViewById<TextView>(R.id.textViewBack)            // 돌아가기 버튼 ID

        val dbHelper = UserDBHelper(this)
        val db = dbHelper.readableDatabase

        clickButton.setOnClickListener {
            val id = idEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()

            // 유효성 검사
            when {
                id.isEmpty() || email.isEmpty() -> {
                    Toast.makeText(this, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "유효한 이메일 형식을 입력해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val cursor = db.rawQuery(
                "SELECT password FROM user WHERE id = ? AND email = ?",
                arrayOf(id, email)
            )

            if (cursor.moveToFirst()) {
                val password = cursor.getString(0)
                resultTextView.text = "찾은 비밀번호: $password"
                resultTextView.visibility = View.VISIBLE
            } else {
                resultTextView.text = "해당 정보로 등록된 비밀번호가 없습니다"
                resultTextView.visibility = View.VISIBLE
            }

            cursor.close()
        }

        backText.setOnClickListener {
            finish() // 이전 화면으로 돌아가기
        }

    }
}
