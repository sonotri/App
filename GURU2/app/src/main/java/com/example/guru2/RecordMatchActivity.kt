package com.example.guru2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class RecordMatchActivity : AppCompatActivity() {

    private lateinit var spinnerDate: Spinner
    private lateinit var spinnerTeam1: Spinner
    private lateinit var spinnerTeam2: Spinner
    private lateinit var spinnerStadium: Spinner
    private lateinit var radioViewType: RadioGroup
    private lateinit var radioResult: RadioGroup
    private lateinit var editScore1: EditText
    private lateinit var editScore2: EditText
    private lateinit var editReview: EditText
    private lateinit var btnSave: Button
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_match)

        // View 연결
        spinnerDate = findViewById(R.id.btnDate)
        spinnerTeam1 = findViewById(R.id.spinnerTeam1)
        spinnerTeam2 = findViewById(R.id.spinnerTeam2)
        spinnerStadium = findViewById(R.id.spinnerStadium)
        radioViewType = findViewById(R.id.radioViewType)
        radioResult = findViewById(R.id.radioResult)
        editScore1 = findViewById(R.id.editScore1)
        editScore2 = findViewById(R.id.editScore2)
        editReview = findViewById(R.id.editReview)
        btnSave = findViewById(R.id.btnSave)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val dateList = mutableListOf("날짜를 선택하세요")

        repeat(7) {
            dateList.add(sdf.format(calendar.time))
            calendar.add(Calendar.DATE, -1)
        }

        // Spinner 샘플 값 초기화
        val teams = arrayOf(
            "팀을 선택하세요",
            "아스날", "맨체스터 시티", "리버풀", "첼시", "맨체스터 유나이티드",
            "토트넘", "뉴캐슬", "애스턴 빌라", "브라이턴", "울버햄튼",
            "웨스트햄", "크리스탈 팰리스", "에버턴", "브렌트포드", "풀럼",
            "본머스", "노팅엄 포레스트", "사우스햄튼", "리즈", "레스터"
        )
        val stadiums = arrayOf(
            "구장을 선택하세요",
            "에미레이츠 스타디움", "에티하드 스타디움", "안필드", "올드 트래퍼드", "스탬포드 브릿지",
            "토트넘 홋스퍼 스타디움", "세인트 제임스 파크", "빌라 파크", "굿디슨 파크"
        )

        val dateAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dateList) {
            override fun isEnabled(position: Int): Boolean = position != 0
        }
        val teamAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, teams) {
            override fun isEnabled(position: Int): Boolean = position != 0 // 첫 번째는 선택 불가
        }
        val stadiumAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stadiums) {
            override fun isEnabled(position: Int): Boolean = position != 0 // 첫 항목 선택 불가
        }

        spinnerDate.adapter = dateAdapter

        intent.getStringExtra("date")?.let { receivedDate ->
            val dateIndex = dateList.indexOf(receivedDate)
            if (dateIndex != -1) {
                spinnerDate.setSelection(dateIndex)
            }
        }

        spinnerTeam1.adapter = teamAdapter
        spinnerTeam2.adapter = teamAdapter
        spinnerTeam1.setSelection(0)
        spinnerTeam2.setSelection(0)
        spinnerStadium.adapter = stadiumAdapter
        spinnerStadium.setSelection(0)

        // 저장 버튼 클릭
        btnSave.setOnClickListener {
            selectedDate = spinnerDate.selectedItem.toString()
            val team1 = spinnerTeam1.selectedItem.toString()
            val team2 = spinnerTeam2.selectedItem.toString()
            val stadium = spinnerStadium.selectedItem.toString()
            val viewType = if (findViewById<RadioButton>(R.id.radioLive).isChecked) "직관" else "집관"
            val resultEmoji = when {
                findViewById<RadioButton>(R.id.radioWin).isChecked -> "🏆"
                findViewById<RadioButton>(R.id.radioDraw).isChecked -> "🟰"
                findViewById<RadioButton>(R.id.radioLose).isChecked -> "🥲"
                else -> ""
            }
            val review = editReview.text.toString()
            val score1 = editScore1.text.toString()
            val score2 = editScore2.text.toString()

            if (selectedDate == "날짜를 선택하세요") {
                Toast.makeText(this, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val validTeam = team1 != "팀을 선택하세요" && team2 != "팀을 선택하세요"
            val validStadium = stadium != "구장을 선택하세요"
            val viewTypeValid = radioViewType.checkedRadioButtonId != -1
            val resultValid = radioResult.checkedRadioButtonId != -1

            if (!validTeam || !validStadium || stadium.isBlank() || review.isBlank() || score1.isBlank() || score2.isBlank() || !viewTypeValid || !resultValid) {
                Toast.makeText(this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (team1 == team2) {
                Toast.makeText(this, "팀1과 팀2는 서로 달라야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val record = JSONObject().apply {
                put("team1", team1)
                put("team2", team2)
                put("stadium", stadium)
                put("viewType", viewType)
                put("resultEmoji", resultEmoji)
                put("review", review)
                put("score1", score1)
                put("score2", score2)
            }

            val prefs = getSharedPreferences("MatchRecords", Context.MODE_PRIVATE)
            prefs.edit().putString(selectedDate, record.toString()).apply()

            for ((k, v) in prefs.all) {
                Log.d("저장 확인", "$k → $v")
            }

            Toast.makeText(this, "기록 저장 완료", Toast.LENGTH_SHORT).show()
            finish()
        }

        // 하단 메뉴 버튼 연결
        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        val btnSchedule = findViewById<ImageButton>(R.id.btn_schedule)
        val btnPlayer = findViewById<ImageButton>(R.id.btn_player)
        val btnLocation = findViewById<ImageButton>(R.id.btn_location)
        val btnProfile = findViewById<ImageButton>(R.id.btn_profile)

        btnHome.setOnClickListener {}
        btnSchedule.setOnClickListener {
            startActivity(Intent(this, ScheduleActivity::class.java))
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
        btnHome.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
