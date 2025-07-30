package com.example.guru2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleActivity : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        calendarView = findViewById(R.id.calendarView)
        val btnWrite = findViewById<Button>(R.id.btnWrite)

        // 오늘 날짜로 기본 선택 설정
        calendarView.selectedDate = CalendarDay.today()

        // 하단 메뉴 버튼 연결
        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        val btnPlayer = findViewById<ImageButton>(R.id.btn_player)
        val btnLocation = findViewById<ImageButton>(R.id.btn_location)
        val btnProfile = findViewById<ImageButton>(R.id.btn_profile)

        btnWrite.setOnClickListener {
            val selectedDate = calendarView.selectedDate
            if (selectedDate == null) {
                Toast.makeText(this, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dateStr = String.format(
                Locale.KOREA,
                "%04d-%02d-%02d",
                selectedDate.year,
                selectedDate.month,
                selectedDate.day
            )
            val intent = Intent(this, RecordMatchActivity::class.java)
            intent.putExtra("date", dateStr)
            intent.putExtra("editMode", true)
            startActivity(intent)
        }

        // 툴바 연동
        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnPlayer.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
        btnLocation.setOnClickListener {
            startActivity(Intent(this, LocationActivity::class.java))
        }
        btnProfile.setOnClickListener {
            startActivity(Intent(this, MypageActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        calendarView.removeDecorators()

        val prefs = getSharedPreferences("MatchRecords", Context.MODE_PRIVATE)
        val emojiMap = mutableMapOf<CalendarDay, String>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for ((dateStr, recordStr) in prefs.all) {
            Log.d("DEBUG", "날짜 확인: $dateStr")
            try {
                if (dateStr.isNullOrBlank()) continue
                val date = sdf.parse(dateStr) ?: continue
                val cal = Calendar.getInstance().apply { time = date }
                val calendarDay = CalendarDay.from(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) +1,
                    cal.get(Calendar.DAY_OF_MONTH)
                )
                val emoji = JSONObject(recordStr as String).getString("resultEmoji")
                emojiMap[calendarDay] = emoji
                Log.d("캘린더 저장된 날짜", "$calendarDay → $emoji")
                Log.d("캘린더 선택된 날짜", "${calendarView.selectedDate}")
            } catch (e: Exception) {
                continue
            }
        }

        for ((calendarDay, emoji) in emojiMap) {
            calendarView.addDecorator(EmojiDecorator(calendarDay, emoji))
        }
    }
}