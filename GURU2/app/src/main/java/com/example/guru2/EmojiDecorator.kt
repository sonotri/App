package com.example.guru2

import android.text.SpannableString
import android.util.Log
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class EmojiDecorator(
    private val targetDay: CalendarDay,
    private val emoji: String
) : DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay): Boolean {
        Log.d("데코레이터 비교", "현재: $day / 타겟: $targetDay → ${day == targetDay}")
        return day == targetDay
    }

    override fun decorate(view: DayViewFacade) {
        val spannable = SpannableString(emoji)
        spannable.setSpan(EmojiSpan(emoji), 0, emoji.length, 0)
        view.addSpan(spannable)
    }
}
