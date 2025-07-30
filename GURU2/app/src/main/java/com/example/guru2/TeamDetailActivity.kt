package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class TeamDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_detail)

        val teamName = intent.getStringExtra("teamName") ?: "Unknown"
        val textTeamName = findViewById<TextView>(R.id.textTeamName)
        val teamBanner = findViewById<ImageView>(R.id.teamBanner)
        textTeamName.text = teamName

        val teamBannerResId = when (teamName) {
            "Arsenal" -> R.drawable.arsenal_b
            "Aston Villa" -> R.drawable.aston_villa_b
            "Bournemouth" -> R.drawable.bournemouth_b
            "Brentford" -> R.drawable.brentford_b
            "Brighton" -> R.drawable.brighton_b
            "Burnley" -> R.drawable.burnley_b
            "Chelsea" -> R.drawable.chelsea_b
            "Crystal Palace" -> R.drawable.crystal_palace_b
            "Everton" -> R.drawable.everton_b
            "Fulham" -> R.drawable.fulham_b
            "Ipswich" -> R.drawable.ipswitch_b
            "Leeds United" -> R.drawable.leeds_b
            "Liverpool" -> R.drawable.liverpool_b
            "Manchester City" -> R.drawable.mancity_b
            "Manchester United" -> R.drawable.manu_b
            "Newcastle United" -> R.drawable.newcastle_b
            "Nottingham Forest" -> R.drawable.nottingham_b
            "Tottenham Hotspur" -> R.drawable.tottenham_b
            "West Ham United" -> R.drawable.westham_b
            "Wolverhampton" -> R.drawable.wolves_b
            else -> R.drawable.default_b
        }

        teamBanner.setImageResource(teamBannerResId)

        val upcomingCard = findViewById<CardView>(R.id.cardUpcoming)
        val recentCard = findViewById<CardView>(R.id.cardRecent)
        val playersCard = findViewById<CardView>(R.id.cardPlayers)

        upcomingCard.setOnClickListener {
            val intent = Intent(this, UpcomingMatchActivity::class.java)
            intent.putExtra("teamName", teamName)
            startActivity(intent)
        }

        recentCard.setOnClickListener {
            val intent = Intent(this, RecentResultActivity::class.java)
            intent.putExtra("teamName", teamName)
            startActivity(intent)
        }

        playersCard.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java)
            intent.putExtra("teamName", teamName)
            startActivity(intent)
        }
    }
}
