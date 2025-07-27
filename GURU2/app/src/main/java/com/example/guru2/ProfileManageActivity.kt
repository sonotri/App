package com.example.guru2

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

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
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        if (userId == null) {
            Toast.makeText(this, "로그인 정보가 없습니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

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

        // 변경 버튼 클릭
        buttonUpdate.setOnClickListener {
            val currentPw = editCurrentPassword.text.toString()
            val newPw = editPassword.text.toString()
            val newEmail = editEmail.text.toString()
            val newNickname = editNickname.text.toString()

            // 현재 비밀번호 확인
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

            // 업데이트 실행
            val dbUpdate = dbHelper.writableDatabase
            val updateQuery = "UPDATE user SET email = ?, nickname = ?, password = ? WHERE id = ?"
            dbUpdate.execSQL(updateQuery, arrayOf(newEmail, newNickname, newPw, userId))
            dbUpdate.close()

            Toast.makeText(this, "회원정보가 수정되었습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

