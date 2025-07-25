package com.example.guru2

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class FindIdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_id)

        val emailEditText = findViewById<EditText>(R.id.editTextId)         // email
        val nicknameEditText = findViewById<EditText>(R.id.editTextPassword)// 닉네임
        val resultTextView = findViewById<TextView>(R.id.textViewResult)    // 결과 출력 텍스트
        val clickButton = findViewById<Button>(R.id.buttonLogin)            // 버튼
        val backText = findViewById<TextView>(R.id.textViewBack)            // 돌아가기 버튼 ID

        val dbHelper = UserDBHelper(this)
        val db = dbHelper.readableDatabase

        clickButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val nickname = nicknameEditText.text.toString().trim()

            // 유효성 검사
            when {
                email.isEmpty() || nickname.isEmpty() -> {
                    Toast.makeText(this, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "유효한 이메일 형식을 입력해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val cursor = db.rawQuery(
                "SELECT id FROM user WHERE email = ? AND nickname = ?",
                arrayOf(email, nickname)
            )

            if (cursor.moveToFirst()) {
                val userId = cursor.getString(0)
                resultTextView.text = "찾은 아이디: $userId"
                resultTextView.visibility = View.VISIBLE
            } else {
                resultTextView.text = "해당 정보로 등록된 아이디가 없습니다"
                resultTextView.visibility = View.VISIBLE
            }

            cursor.close()
        }

        backText.setOnClickListener {
            finish() // 이전 화면으로 돌아가기
        }
    }
}
