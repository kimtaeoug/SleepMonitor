package ted.gun0912.sleep.ui.widget

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.widget.NumberPicker
import android.widget.TimePicker
import ted.gun0912.sleep.common.util.loge

class SleepTimePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TimePicker(context, attrs) {

    init {
        setInterval()
    }

    private fun setInterval() {
        try {
            val minuteId = Resources.getSystem().getIdentifier(
                "minute",
                "id",
                "android"
            )
            val minutePicker: NumberPicker = findViewById(minuteId)
            minutePicker.apply {
                minValue = 0
                maxValue = 60 / MINUTE_INTERVAL - 1
                displayedValues = getDisplayedValue()
            }
        } catch (e: Exception) {
            loge(e.toString())
            e.printStackTrace()
        }
    }

    override fun setMinute(minute: Int) {
        super.setMinute(minute / MINUTE_INTERVAL)
    }

    override fun getMinute(): Int = super.getMinute() * MINUTE_INTERVAL

    override fun setOnTimeChangedListener(onTimeChangedListener: OnTimeChangedListener) {
        super.setOnTimeChangedListener { view, hourOfDay, _ ->
            val adjustMinute = view.minute
            onTimeChangedListener.onTimeChanged(view, hourOfDay, adjustMinute)
        }
    }

    private fun getDisplayedValue(): Array<String> {
        val minutesArray = ArrayList<String>()
        for (i in 0 until 60 step MINUTE_INTERVAL) {
            minutesArray.add(String.format("%02d", i))
        }
        return minutesArray.toArray(arrayOf(""))
    }

    companion object {
        private const val MINUTE_INTERVAL = 1
    }
}
