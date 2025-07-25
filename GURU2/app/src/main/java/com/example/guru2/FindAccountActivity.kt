package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class FindAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_account)

        val cardFindId = findViewById<CardView>(R.id.cardFindId)            // 아이디 찾기 버튼
        val cardFindPw = findViewById<CardView>(R.id.cardFindPassword)      // 비밀번호 찾기 버튼
        val backText = findViewById<TextView>(R.id.textViewBack)            // 돌아가기 버튼 ID

        cardFindId.setOnClickListener {
            val intent = Intent(this, FindIdActivity::class.java)
            startActivity(intent)
        }

        cardFindPw.setOnClickListener {
            val intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }

        backText.setOnClickListener {
            finish() // 이전 화면으로 돌아가기
        }
    }
}
