package com.example.guru2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class RecordMatchActivity : AppCompatActivity() {

    private lateinit var dbHelper: MatchDBHelper
    private var matchId: Int? = null

    private lateinit var btnDelete: Button
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

        dbHelper = MatchDBHelper(this)

        // View 연결
        btnDelete = findViewById(R.id.btnDelete)
        spinnerDate = findViewById(R.id.btnDate)
        spinnerTeam1 = findViewById(R.id.spinnerTeam1)
        spinnerTeam2 = findViewById(R.id.spinnerTeam2)
        spinnerStadium = findViewById(R.id.spinnerStadium)
        radioViewType = findViewById(R.id.radioViewType)
        editScore1 = findViewById(R.id.editScore1)
        editScore2 = findViewById(R.id.editScore2)
        editReview = findViewById(R.id.editReview)
        btnSave = findViewById(R.id.btnSave)
        radioResult = findViewById(R.id.radioResult)

        // 날짜 리스트 생성
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val today = Date()

        calendar.set(2025, Calendar.JANUARY, 1) // 시작일: 2025년 1월 1일
        val dateMap = mutableListOf<String>()
        val dateList = mutableListOf("날짜를 선택하세요")
        while (calendar.time <= today) {
            dateList.add(sdf.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        dateList.addAll(dateMap)

        // 어댑터 설정 및 연결
        val dateAdapter = object :
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dateList) {
            override fun isEnabled(position: Int): Boolean = position != 0 // 첫 항목 선택 불가
        }
        spinnerDate.adapter = dateAdapter

        spinnerDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedDate = parent.getItemAtPosition(position).toString()
                Log.d("날짜선택", "선택된 날짜: $selectedDate") // 로그 추가
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedDate = null
            }
        }

        // Spinner 샘플 값 초기화
        val teams = arrayOf(
            "팀을 선택하세요",
            "노팅엄 포레스트", "뉴캐슬", "리버풀", "리즈", "맨체스터 시티", "맨체스터 유나이티드",
            "번리", "본머스", "브라이튼 앤 호브 알비온", "브렌트포드", "선덜랜드", "아스날",
            "아스톤 빌라", "에버튼", "울버햄튼 원더러스", "웨스트햄 유나이티드", "첼시",
            "토트넘 홋스퍼", "크리스털 팰리스", "풀럼"
        )
        val stadiums = arrayOf(
            "구장을 선택하세요",
            "시티 그라운드", "세인트 제임스 파크", "안필드", "엘런드 로드", "에티하드 스타디움",
            "올드 트래포드", "터프 무어", "바이탈리티 스타디움", "아메리칸 익스프레스 스타디움",
            "지테크 커뮤니티 스타디움", "스타디움 오브 라이트", "에미레이트 스타디움", "빌라 파크",
            "힐 디킨슨 스타디움", "몰리뉴 스타디움", "런던 스타디움", "스탬퍼드 브리지", "토트넘 홋스퍼 스타디움",
            "셀허스트 파크", "크레이븐 코티지"
        )
        val teamAdapter = object :
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, teams) {
            override fun isEnabled(position: Int): Boolean = position != 0 // 첫 항목 선택 불가
        }
        val stadiumAdapter = object :
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stadiums) {
            override fun isEnabled(position: Int): Boolean = position != 0 // 첫 항목 선택 불가
        }

        spinnerTeam1.adapter = teamAdapter
        spinnerTeam2.adapter = teamAdapter
        spinnerTeam1.setSelection(0)
        spinnerTeam2.setSelection(0)
        spinnerStadium.adapter = stadiumAdapter
        spinnerStadium.setSelection(0)

        // 날짜 설정 및 editMode 처리
        val receivedDate = intent.getStringExtra("date")
        val editMode = intent.getBooleanExtra("editMode", false)
        if (receivedDate != null) {
            val dateIndex = dateList.indexOf(receivedDate)
            Log.d("날짜전달", "받은 날짜: $receivedDate / index: $dateIndex")
            if (dateIndex != -1) {
                spinnerDate.setSelection(dateIndex, false)
                selectedDate = receivedDate
            }
            val matches = dbHelper.getMatchByDate(receivedDate)
            if (editMode && matches.isNotEmpty()) {
                val match = matches.first()
                matchId = match.id
                spinnerTeam1.setSelection(teams.indexOf(match.team1))
                spinnerTeam2.setSelection(teams.indexOf(match.team2))
                spinnerStadium.setSelection(stadiums.indexOf(match.stadium))
                editScore1.setText(match.score1.toString())
                editScore2.setText(match.score2.toString())
                editReview.setText(match.review)
                when (match.viewType) {
                    "직관" -> radioViewType.check(R.id.radioLive)
                    "집관" -> radioViewType.check(R.id.radioHome)
                }
            }
        } else {
            // 받은 날짜 없으면 오늘 날짜로 선택
            val todayStr = sdf.format(today)
            val todayIndex = dateList.indexOf(todayStr)
            if (todayIndex != -1) {
                spinnerDate.setSelection(todayIndex)
                selectedDate = todayStr
            }
        }

        btnDelete.visibility = if (editMode) View.VISIBLE else View.GONE

        // 저장 버튼 클릭
        btnSave.setOnClickListener {
            selectedDate = spinnerDate.selectedItem.toString()
            val team1 = spinnerTeam1.selectedItem.toString()
            val team2 = spinnerTeam2.selectedItem.toString()
            val stadium = spinnerStadium.selectedItem.toString()
            val score1 = editScore1.text.toString()
            val score2 = editScore2.text.toString()
            val review = editReview.text.toString()
            val viewType =
                if (radioViewType.checkedRadioButtonId == R.id.radioLive) "직관" else "집관"

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

            if (editMode && matchId != null) {
                dbHelper.updateMatch(
                    id = matchId!!,
                    team1 = team1, team2 = team2,
                    stadium = stadium, viewType = viewType,
                    score1 = score1.toInt(),
                    score2 = score2.toInt(),
                    review = review
                )
                Toast.makeText(this, "기록 수정 완료", Toast.LENGTH_SHORT).show()
            } else {
                dbHelper.insertMatch(
                    date = selectedDate!!, team1 = team1, team2 = team2,
                    stadium = stadium, viewType = viewType,
                    score1 = score1.toInt(),
                    score2 = score2.toInt(),
                    review = review
                )
                Toast.makeText(this, "기록 저장 완료", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

        // 삭제 버튼 클릭
        btnDelete.setOnClickListener {
            matchId?.let {
                dbHelper.deleteMatchById(it)
                Toast.makeText(this, "기록 삭제 완료", Toast.LENGTH_SHORT).show()

                val resultIntent = Intent()
                resultIntent.putExtra("deleted", true)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
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
