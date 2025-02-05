package ted.gun0912.sleep.ui.feature.history.calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.MonthView
import kotlin.math.min

class HistoryMonthView(context: Context) : MonthView(context) {

    private var radius: Float = 0f

    override fun onPreviewHook() {
        radius = (min(mItemWidth, mItemHeight) / 5 * 2.2).toFloat()
        mSchemePaint.style = Paint.Style.STROKE
    }

    override fun onDrawSelected(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean
    ): Boolean {
        val cx = x + mItemWidth / 2
        val cy = y + mItemHeight / 2
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius, mSelectedPaint)
        return false
    }

    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int, y: Int) {
        val cx = x + mItemWidth / 2
        val cy = y + mItemHeight / 2
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius, mSchemePaint)
    }

    override fun onDrawText(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        val baselineY = mTextBaseLine + y
        val cx = x + mItemWidth / 2

        if (isSelected) {
            canvas.drawText(
                calendar.day.toString(),
                cx.toFloat(),
                baselineY,
                mSelectTextPaint
            )
        } else if (hasScheme) {
            canvas.drawText(
                calendar.day.toString(),
                cx.toFloat(),
                baselineY,
                if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth) mSchemeTextPaint else mOtherMonthTextPaint
            )
        } else {
            canvas.drawText(
                calendar.day.toString(), cx.toFloat(), baselineY,
                if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth) mCurMonthTextPaint else mOtherMonthTextPaint
            )
        }
    }
}
