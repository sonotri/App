package com.example.guru2

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

class EmojiSpan(private val emoji: String) : ReplacementSpan() {

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        paint.textSize = 40f
        return paint.measureText(emoji).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        paint.textSize = 40f
        paint.isAntiAlias = true
        canvas.drawText(emoji, x, y.toFloat(), paint)
    }
}
