package com.example.guru2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MatchDBHelper(context: Context) : SQLiteOpenHelper(context, "MatchDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE match_record (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT,
                team1 TEXT,
                team2 TEXT,
                stadium TEXT,
                view_type TEXT,
                score1 INTEGER,
                score2 INTEGER,
                review TEXT
            )
            """.trimIndent()
        )
    }

    fun insertMatch(
        date: String,
        team1: String,
        team2: String,
        stadium: String,
        viewType: String,
        score1: Int,
        score2: Int,
        review: String
    ) {
        val db = writableDatabase
        val sql = """
        INSERT INTO match_record (date, team1, team2, stadium, view_type, score1, score2, review)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """.trimIndent()
        val args = arrayOf(date, team1, team2, stadium, viewType, score1, score2, review)
        db.execSQL(sql, args)
        db.close()
    }

    fun getMatchByDate(date: String): List<RecordMatch> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM match_record WHERE date = ?", arrayOf(date))
        val result = mutableListOf<RecordMatch>()

        while (cursor.moveToNext()) {
            val match = RecordMatch(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                date = cursor.getString(cursor.getColumnIndexOrThrow("date")),
                team1 = cursor.getString(cursor.getColumnIndexOrThrow("team1")),
                team2 = cursor.getString(cursor.getColumnIndexOrThrow("team2")),
                stadium = cursor.getString(cursor.getColumnIndexOrThrow("stadium")),
                viewType = cursor.getString(cursor.getColumnIndexOrThrow("view_type")),
                score1 = cursor.getInt(cursor.getColumnIndexOrThrow("score1")),
                score2 = cursor.getInt(cursor.getColumnIndexOrThrow("score2")),
                review = cursor.getString(cursor.getColumnIndexOrThrow("review"))
            )
            result.add(match)
        }
        cursor.close()
        db.close()
        return result
    }

    fun updateMatch(
        id: Int,
        team1: String,
        team2: String,
        stadium: String,
        viewType: String,
        score1: Int,
        score2: Int,
        review: String
    ) {
        val db = writableDatabase
        val sql = """
        UPDATE match_record SET
            team1 = ?, team2 = ?, stadium = ?, view_type = ?,
            score1 = ?, score2 = ?, review = ?
        WHERE id = ?
    """.trimIndent()
        val args = arrayOf(team1, team2, stadium, viewType, score1, score2, review, id)
        db.execSQL(sql, args)
        db.close()
    }

    fun deleteMatchById(id: Int) {
        val db = writableDatabase
        db.execSQL("DELETE FROM match_record WHERE id = ?", arrayOf(id))
        db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS match_record")
        onCreate(db)
    }
}
