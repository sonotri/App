package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileDeleteActivity : AppCompatActivity() {

    private lateinit var buttonWithdraw: Button
    private lateinit var checkBox1: CheckBox
    private lateinit var checkBox2: CheckBox
    private lateinit var checkBox3: CheckBox
    private lateinit var checkBox4: CheckBox

    // 툴바
    private lateinit var buttonSchedule: ImageButton
    private lateinit var buttonPlayer: ImageButton
    private lateinit var buttonHome: ImageButton
    private lateinit var buttonLocation: ImageButton
    private lateinit var buttonProfile: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_profile)

        buttonWithdraw = findViewById(R.id.buttonWithdraw)
        checkBox1 = findViewById(R.id.checkBox1)
        checkBox2 = findViewById(R.id.checkBox2)
        checkBox3 = findViewById(R.id.checkBox3)
        checkBox4 = findViewById(R.id.checkBox4)

        // 툴바 버튼 연동
        buttonSchedule = findViewById(R.id.btn_schedule)
        buttonPlayer = findViewById(R.id.btn_player)
        buttonHome = findViewById(R.id.btn_home)
        buttonLocation = findViewById(R.id.btn_location)
        buttonProfile = findViewById(R.id.btn_profile)

        // 다음으로 버튼 클릭
        buttonWithdraw.setOnClickListener {
            if (checkBox1.isChecked && checkBox2.isChecked && checkBox3.isChecked && checkBox4.isChecked) {
            val intent = Intent(this, RealProfileDeleteActivity::class.java)
            startActivity(intent)
            } else {
                Toast.makeText(this, "모든 항목에 동의해야 진행할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 툴바 버튼 리스너
        buttonSchedule.setOnClickListener {
            val intent = Intent(this, ScheduleActivity::class.java)
            startActivity(intent)
        }

        buttonPlayer.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }

        buttonHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        buttonLocation.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }

        buttonProfile.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }

}
