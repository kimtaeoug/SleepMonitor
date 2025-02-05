package ted.gun0912.sleep.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon
import com.skydoves.balloon.showAlignBottom
import ted.gun0912.sleep.model.DailySleepStatistic
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.databinding.ViewDailySleepSummeryBinding
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class DailySleepSummaryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewDailySleepSummeryBinding by lazy {
        ViewDailySleepSummeryBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        if (isInEditMode) {
            inflate(context, R.layout.view_daily_sleep_summery, this)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding.view = this
    }

    fun setDailySleepStatistic(dailySleepStatistic: DailySleepStatistic?) {
        dailySleepStatistic ?: return
        binding.isSingleData = true
        setSummaryTextInfo(listOf(dailySleepStatistic))
    }

    fun setDailySleepStatistics(dailySleepStatistics: List<DailySleepStatistic>?) {
        dailySleepStatistics ?: return
        binding.isSingleData = false
        setSummaryTextInfo(dailySleepStatistics)
    }

    private fun setSummaryTextInfo(dailySleepStatistics: List<DailySleepStatistic>) {
        val scoreUnit = context.getString(R.string.score)
        val countUnit = context.getString(R.string.count)
        val indexValue = context.getString(R.string.indexValue)
        val bpm = context.getString(R.string.bpm)

        val sleepScoreText = dailySleepStatistics.getSleepScoreText({ it.sleepScore }, scoreUnit)
        val heartRateText = dailySleepStatistics.getAverageText({ it.heartRate }, bpm)
        val averageSleepDurationText =
            dailySleepStatistics.getSleepDurationText { it.average().toLong() }
        val sleepEfficiencyText =
            dailySleepStatistics.getAverageText({ it.sleepEfficiency }, indexValue)
        val sleepTimeText = dailySleepStatistics.getAverageTimeText { it.sleepTime }
        val wakeUpTimeText = dailySleepStatistics.getAverageTimeText { it.wakeUpTime }
        val movingText = dailySleepStatistics.getAverageText({ it.moving }, countUnit)
        val movingIndex = dailySleepStatistics.getAverageText({ it.movingIndex }, indexValue)
        val respiratoryDistressText =
            dailySleepStatistics.getRespDistressText({ it.rdi }, indexValue)
        val totalSleepDurationText = dailySleepStatistics.getSleepDurationText { it.sum() }
        val totalMeasureDurationText = dailySleepStatistics.getTotalMeasureDurationText()

        // todo mapping
        val averageHumidityText = dailySleepStatistics.getOrNull(0)?.avgHumidity?.toString() ?: "결과 없음"
        val averageTvocText = dailySleepStatistics.getOrNull(0)?.avgTvoc?.toString() ?: "결과 없음"
        val averageTemperatureText = dailySleepStatistics.getOrNull(0)?.avgTemperature?.toString() ?: "결과 없음"
        val averageCo2Text = dailySleepStatistics.getOrNull(0)?.avgCo2?.toString() ?: "결과 없음"

        val sleepInterviewScoreText = dailySleepStatistics.getOrNull(0)?.sleepInterviewScore
            ?.let { score -> score.toString() + "점" } ?: "결과 없음"
//            dailySleepStatistics.getAverageText({ it.sleepInterviewScore ?: 0 }, scoreUnit)
        val painInterviewScoreText = dailySleepStatistics.getOrNull(0)?.painInterviewScore
            ?.let { score -> score.toString() + "점" } ?: "결과 없음"
//        dailySleepStatistics.getAverageText({ it.painInterviewScore ?: 0 }, scoreUnit)

        binding.summaryTextInfo = SummaryTextInfo(
            sleepScoreText,
            heartRateText,
            averageSleepDurationText,
            sleepEfficiencyText,
            sleepTimeText,
            wakeUpTimeText,
            movingText,
            movingIndex,
            respiratoryDistressText,
            totalSleepDurationText,
            totalMeasureDurationText,

            averageHumidityText,
            averageTvocText,
            averageTemperatureText,
            averageCo2Text,

            sleepInterviewScoreText,
            painInterviewScoreText
        )
    }

    private fun List<DailySleepStatistic>.getRespDistressText(
        mapper: (DailySleepStatistic) -> Int,
        suffix: String
    ): String {
        val score = map { mapper(it) }.average().toInt()
        val scoreText = when {
            score <= 5 -> context.getString(R.string.great)
            score > 5 && score >= 10 -> context.getString(R.string.notbad)
            else -> context.getString(R.string.bad)
        }
        return scoreText
    }

    private fun List<DailySleepStatistic>.getSleepScoreText(
        mapper: (DailySleepStatistic) -> Int,
        suffix: String,
    ): String {
        val score = map { mapper(it) }.average().toInt()
        val scoreText: String = when {
            score > 87 -> "(" + context.getString(R.string.great) + ")"
            score > 75 -> "(" + context.getString(R.string.notbad) + ")"
            score > 50 -> "(" + context.getString(R.string.bad) + ")"
            else -> "(" + context.getString(R.string.verybad) + ")"
        }
        return "$score $suffix $scoreText"
    }

    private fun List<DailySleepStatistic>.getAverageText(
        mapper: (DailySleepStatistic) -> Int,
        suffix: String,
    ): String {
        val average = map { mapper(it) }.average().toInt()
        return "$average $suffix"
    }

    private fun List<DailySleepStatistic>.getAverageTimeText(
        mapper: (DailySleepStatistic) -> LocalDateTime,
    ): String {
        val averageSecond = map { mapper(it).toLocalTime().toSecondOfDay() }.average()
        val averageLocalTime = LocalTime.ofSecondOfDay(averageSecond.toLong())
        return DateTimeFormatter.ofPattern("a hh:mm").format(averageLocalTime)
    }

    private fun List<DailySleepStatistic>.getSleepDurationText(mapper: (List<Long>) -> Long): String {
        val sleepDurationSeconds = map { it.realSleepDuration.seconds }
        val resultSecond = mapper(sleepDurationSeconds)
        val durationMinute = TimeUnit.SECONDS.toMinutes(resultSecond).toInt()
        val hour = durationMinute / 60
        val minute = durationMinute % 60
        val durationHourUnit = context.getString(R.string.duration_hour)
        val minuteUnit = context.getString(R.string.minute)
        return "${hour}$durationHourUnit ${minute}$minuteUnit"
    }

    private fun List<DailySleepStatistic>.getTotalMeasureDurationText(): String {
        val hour = map { (it.sleepMeasureDuration.toMillis() / (60 * 60 * 1000L)) % 24 }.toString()
            .replace("[", "").replace("]", "")
        val minute = map { (it.sleepMeasureDuration.toMillis() / (60 * 1000L) % 60) }.toString()
            .replace("[", "").replace("]", "")

        val durationHourUnit = context.getString(R.string.duration_hour)
        val minuteUnit = context.getString(R.string.minute)

        return "${hour}$durationHourUnit ${minute}$minuteUnit"
    }

    fun showToolTip(view: View, text: String) {
        val balloon = createBalloon(context) {
            setText(text)
            setTextColorResource(android.R.color.black)
            setTextSize(18f)
            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            setArrowSize(10)
            setArrowPosition(0.5f)
            setPadding(8)
            setCornerRadius(8f)
            setBackgroundColorResource(R.color.bg_tooltip)
            setBalloonAnimation(BalloonAnimation.ELASTIC)
            setLifecycleOwner(findViewTreeLifecycleOwner())
            build()
        }
        view.showAlignBottom(balloon)
    }

    data class SummaryTextInfo(
        val sleepScoreText: String,
        val heartRateText: String,
        val averageSleepDurationText: String,
        val sleepEfficiencyText: String,
        val sleepTimeText: String,
        val wakeUpTimeText: String,
        val movingText: String,
        val movingIndexText: String,
        val respiratoryDistressText: String,
        val totalSleepDurationText: String,
        val totalMeasureDurationText: String,

        val averageHumidityText: String,
        val averageTvocText: String,
        val averageTemperatureText: String,
        val averageCo2Text: String,

        val sleepInterviewScoreText: String,
        val painInterviewScoreText: String
    )
}
