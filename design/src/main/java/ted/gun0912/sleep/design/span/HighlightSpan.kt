package ted.gun0912.sleep.design.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan
import kotlin.math.roundToInt

class HighlightSpan(private val backgroundColor: Int) : ReplacementSpan() {
    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return measureText(paint, text, start, end).roundToInt()
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
        val paintColor = paint.color
        val adjustTop = ((1 - HIGHLIGHT_HEIGHT_RATIO) * bottom)
        val rect = RectF(
            x,
            adjustTop,
            x + measureText(paint, text, start, end), bottom.toFloat()
        )
        paint.color = backgroundColor
        canvas.drawRect(rect, paint)
        paint.color = paintColor
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }

    private fun measureText(paint: Paint, text: CharSequence, start: Int, end: Int): Float {
        return paint.measureText(text, start, end)
    }

    companion object {
        private const val HIGHLIGHT_HEIGHT_RATIO = 0.5f
    }
}
