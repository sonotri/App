package com.example.guru2

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class JoinActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var nicknameEditText: EditText
    private lateinit var checkNicknameButton: Button
    private lateinit var idEditText: EditText
    private lateinit var checkIdButton: Button
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var joinButton: Button
    private lateinit var backTextView: TextView

    private var isNicknameChecked = false // 닉네임 중복 확인 여부
    private var isIdChecked = false // ID 중복 확인 여부
    private lateinit var dbHelper: UserDBHelper // DB 헬퍼

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        emailEditText = findViewById(R.id.editTextEmail)
        nicknameEditText = findViewById(R.id.editTextNickname)
        checkNicknameButton = findViewById(R.id.buttonCheckNickname)
        idEditText = findViewById(R.id.editTextId)
        checkIdButton = findViewById(R.id.buttonCheckId)
        passwordEditText = findViewById(R.id.editTextPassword)
        confirmPasswordEditText = findViewById(R.id.editTextPasswordConfirm)
        joinButton = findViewById(R.id.buttonJoin)
        backTextView = findViewById(R.id.textViewBack)

        dbHelper = UserDBHelper(this) // DB 헬퍼 초기화

        // 닉네임 중복확인 버튼 클릭 후 텍스트 수정 시 중복확인 해제
        nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isNicknameChecked = false
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 닉네임 중복확인 버튼 클릭 후 텍스트 수정 시 중복확인 해제
        idEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isIdChecked = false
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 뒤로가기 버튼 클릭 시 로그인 화면으로 이동
        backTextView.setOnClickListener {
            finish()
        }

        // 닉네임 중복 확인 클릭 시 유효성 검사
        checkNicknameButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString().trim()
            if (nickname.isEmpty()) {
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!isValidNickname(nickname)) {
                Toast.makeText(this, "닉네임은 3자 이상 10자 이하의 한글로 구성되어야 합니다", Toast.LENGTH_SHORT).show()
            } else if (isNicknameDuplicate(nickname)) {
                Toast.makeText(this, "이미 사용 중인 닉네임입니다", Toast.LENGTH_SHORT).show()
                isNicknameChecked = false
            } else {
                Toast.makeText(this, "사용 가능한 닉네임입니다", Toast.LENGTH_SHORT).show()
                isNicknameChecked = true
            }
        }

        // ID 중복 확인 클릭 시 유효성 검사
        checkIdButton.setOnClickListener {
            val id = idEditText.text.toString().trim()
            if (id.isEmpty()) {
                Toast.makeText(this, "ID를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!isValidId(id)) {
                Toast.makeText(this, "ID는 4자 이상 20자 이하의 알파벳과 숫자로 구성되어야 합니다", Toast.LENGTH_SHORT).show()
            }  else if (isIdDuplicate(id)) {
                Toast.makeText(this, "이미 사용 중인 ID입니다", Toast.LENGTH_SHORT).show()
                isIdChecked = false
            } else {
                Toast.makeText(this, "사용 가능한 ID입니다", Toast.LENGTH_SHORT).show()
                isIdChecked = true
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

                !isNicknameChecked || isNicknameDuplicate(nickname) ->
                    Toast.makeText(this, "닉네임 중복 확인을 해주세요", Toast.LENGTH_SHORT).show()

                !isIdChecked || isIdDuplicate(userId) ->
                    Toast.makeText(this, "ID 중복 확인을 해주세요", Toast.LENGTH_SHORT).show()

                !isValidNickname(nickname) -> {
                    Toast.makeText(this, "닉네임은 3자 이상 10자 이하의 한글로 구성되어야 합니다", Toast.LENGTH_SHORT).show()
                }

                !isValidId(userId) -> {
                    Toast.makeText(this, "ID는 4자 이상 20자 이하의 알파벳과 숫자로 구성되어야 합니다", Toast.LENGTH_SHORT).show()
                }

                !isValidPassword(password) -> {
                    Toast.makeText(this, "비밀번호는 8자 이상, 특수문자를 포함해야 합니다", Toast.LENGTH_SHORT).show()
                }

                password != confirmPassword ->
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()

                else -> {
                    val db = dbHelper.writableDatabase

                    // 값 저장
                    val values = ContentValues().apply {
                        put("id", userId)
                        put("email", email)
                        put("nickname", nickname)
                        put("password", password)
                    }

                    db.insert("user", null, values)
                    Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                    finish()
                }

            }
        }

    }

    private fun isNicknameDuplicate(nickname: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM user WHERE nickname = ?", arrayOf(nickname))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    private fun isIdDuplicate(id: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM user WHERE id = ?", arrayOf(id))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    private fun isValidNickname(nickname: String): Boolean {
        val idRegex = Regex("^[가-힣]{3,10}$")
        return idRegex.matches(nickname)
    }

    private fun isValidId(id: String): Boolean {
        val idRegex = Regex("^[a-zA-Z0-9]{4,20}$")
        return idRegex.matches(id)
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[!@#\$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#\$%^&*(),.?\":{}|<>]{8,}$")
        return passwordRegex.matches(password)
    }
}