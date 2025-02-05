package ted.gun0912.sleep.ui.feature.deprecated.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import ted.gun0912.rangebarchart.RangeBarChart
import ted.gun0912.rangebarchart.RangeBarData
import ted.gun0912.rangebarchart.RangeBarDataSet
import ted.gun0912.rangebarchart.RangeBarEntry
import ted.gun0912.sleep.model.DailySleepStatistic
import ted.gun0912.sleep.model.RangeDayType
import ted.gun0912.sleep.model.SleepType
import ted.gun0912.sleep.model.extension.DateConverter
import ted.gun0912.sleep.model.extension.between
import ted.gun0912.sleep.model.extension.toSecond
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.BaseFragment
import ted.gun0912.sleep.ui.base.template.FragmentTemplate
import ted.gun0912.sleep.ui.databinding.FragmentDashboardBinding
import ted.gun0912.sleep.ui.extension.getColorInt
import ted.gun0912.sleep.ui.extension.setupChart
import ted.gun0912.sleep.ui.extension.setupColor
import ted.gun0912.sleep.ui.feature.deprecated.dashboard.DashboardViewModel.Event
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DashboardFragment :
    BaseFragment<FragmentDashboardBinding, DashboardViewModel, Event>(R.layout.fragment_dashboard) {

    override val viewModel: DashboardViewModel by viewModels()

    private val durationHourUnit by lazy { getString(R.string.duration_hour) }
    private val hourUnit by lazy { getString(R.string.hour) }
    private val minuteUnit by lazy { getString(R.string.minute) }
    private val countUnit by lazy { getString(R.string.count) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dailySleepStatistics
            .observe(::updateStatistic)
        setupChart()
    }

    override fun handleEvent(event: Event) = Unit

    protected fun setupChart() = with(binding.layoutDashboardContent.layoutDashboardChart) {
        chartSleepScore.setupChart(0f, 100f, { "${it.toInt()}" + getString(R.string.score) })
        chartHeartRate.setupChart(50f, 120f, { "${it.toInt()}" }, { "${it.toInt()}bpm" })
        chartSleepDuration.setupChart(0f, null, { "${it.toInt()}$durationHourUnit" }, {
            val (hour, minute) = getHourMinute(it)
            "${hour}$durationHourUnit ${minute}$minuteUnit"
        })
        chartSleepEfficiency.setupChart(0f, 100f, { "${it.toInt()}%" })
        chartSleepPattern.setupChart(null, null, {
            val localDateTime = DateConverter.secondToLocalDateTime(it.toLong())
            "${localDateTime.hour}$hourUnit"
        })
        chartSleepTime.setupChart(null, null, { "${it.toInt()}$hourUnit" }, {
            val (hour, minute) = getHourMinute(it)
            "${hour}$hourUnit ${minute}$minuteUnit"
        })
        chartWakeUpTime.setupChart(null, null, { "${it.toInt()}$hourUnit" }, {
            val (hour, minute) = getHourMinute(it)
            "${hour}$hourUnit ${minute}$minuteUnit"
        })
        chartMoving.setupChart(0f, null, { "${it.toInt()}$countUnit" })
        chartMovingIndex.setupChart(0f, 100f, { "${it.toInt()}" })
        chartRespiratoryDistress.setupChart(0f, null, { "${it.toInt()}$countUnit" })
    }

    private fun getHourMinute(hourValue: Float): Pair<Int, Int> {
        val totalMinute = (hourValue * 60).toInt()
        val hour = totalMinute / 60
        val minute = totalMinute % 60
        return hour to minute
    }

    protected fun updateStatistic(sleepStatistics: List<DailySleepStatistic>) =
        with(binding.layoutDashboardContent.layoutDashboardChart) {
            if (sleepStatistics.isEmpty()) {
                return
            }
            chartSleepScore.updateData(sleepStatistics) { it.sleepScore.toFloat() }
            chartHeartRate.updateData(sleepStatistics) { it.heartRate.toFloat() }
            chartSleepDuration.updateData(sleepStatistics) { it.realSleepDuration.toMinutes() / 60f }
            chartSleepEfficiency.updateData(sleepStatistics) { it.sleepEfficiency.toFloat() }
            chartSleepPattern.updateSleepPattern(sleepStatistics)
            chartSleepTime.updateData(sleepStatistics) { it.sleepTime.toLocalHour() }
            chartSleepTime.axisLeft.granularity = 1f
            chartWakeUpTime.updateData(sleepStatistics) { it.wakeUpTime.toLocalHour() }
            chartWakeUpTime.axisLeft.granularity = 1f
            chartMoving.updateData(sleepStatistics) { it.moving.toFloat() }
            chartMovingIndex.updateData(sleepStatistics) { it.movingIndex.toFloat() }
            chartRespiratoryDistress.updateData(sleepStatistics) { it.rdi.toFloat() }
        }

    private fun BarChart.updateData(
        sleepStatistics: List<DailySleepStatistic>,
        valueGetter: (DailySleepStatistic) -> Float
    ) {
        val baseEntries = getBaseEntries(sleepStatistics, valueGetter)
        val realEntries = mutableListOf<BarEntry>()
        val emptyEntries = mutableListOf<BarEntry>()
        for (baseEntry in baseEntries) {
            val x = baseEntry.first
            val y = baseEntry.second
            if (y != null) {
                realEntries.add(BarEntry(x, y))
            } else {
                emptyEntries.add(BarEntry(x, -1f))
            }
        }

        val realDataSet = BarDataSet(realEntries, null).apply {
            setupColor(requireContext())
        }
        val emptyDataSet = BarDataSet(emptyEntries, null)
        val barData = BarData(listOf(realDataSet, emptyDataSet)).apply {
            barWidth = BAR_WIDTH
        }
        data = barData
        setupXValueFormatter()
        invalidate()
    }

    private fun LineChart.updateData(
        sleepStatistics: List<DailySleepStatistic>,
        valueGetter: (DailySleepStatistic) -> Float
    ) {
        val baseEntries = getBaseEntries(sleepStatistics, valueGetter)
        val realEntries = mutableListOf<Entry>()
        val emptyEntries = mutableListOf<Entry>()
        for (baseEntry in baseEntries) {
            val x = baseEntry.first
            val y = baseEntry.second
            if (y != null) {
                realEntries.add(Entry(x, y))
            } else {
                emptyEntries.add(Entry(x, -1f))
            }
        }
        with(axisLeft) {
            if (axisMinimum == 0f) {
                axisMinimum =
                    (realEntries.minOf { it.y } - 1).coerceAtLeast(0f)
            }
            if (axisMaximum == 0f) {
                axisMaximum = realEntries.maxOf { it.y } + 1
            }
        }
        val realDataSet = LineDataSet(realEntries, null).apply {
            color = getColorInt(R.color.colorPrimary)
            setCircleColor(getColorInt(R.color.colorPrimary))
            lineWidth = 1f
            circleRadius = 4f
            setDrawCircleHole(false)
        }
        val emptyDataSet = LineDataSet(emptyEntries, null)
        val barData = LineData(listOf(realDataSet, emptyDataSet)).apply {

        }
        data = barData
        setupXValueFormatter()
        invalidate()
    }

    private fun RangeBarChart.updateSleepPattern(
        sleepStatistics: List<DailySleepStatistic>,
    ) {
        val realEntries = mutableListOf<RangeBarEntry>()
        val emptyEntries = mutableListOf<RangeBarEntry>()
        val (startDate, endDate) = viewModel.selectedDays.value
        val duration = startDate.between(endDate)
        for (day in 0..duration.toDays()) {
            val recordDay = startDate.plusDays(day)
            val dailySleepStatistic = sleepStatistics.find { it.recordDay == recordDay }
            if (dailySleepStatistic == null) {
                emptyEntries.add(RangeBarEntry(day.toFloat(), 0f, 0f))
                continue
            }
            val min = dailySleepStatistic.sleepTime.minusDays(day).toSecond().toFloat()
            val max = dailySleepStatistic.wakeUpTime.minusDays(day).toSecond().toFloat()
            val entry = RangeBarEntry(day.toFloat(), min, max)
            realEntries.add(entry)
        }
        setupXValueFormatter()
        val realDataSet = RangeBarDataSet(realEntries, "RangeBar").apply {
            color = getColorInt(R.color.colorPrimary)
            barWidth = BAR_WIDTH
            isMinMaxEnabled = false
            isHighlightEnabled = true
            highLightColor = getColorInt(R.color.colorAccent)

            setDrawValues(false)
            valueTextColor = Color.WHITE
            valueTextSize = 12f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val localDateTime = DateConverter.secondToLocalDateTime(value.toLong())
                    return "${localDateTime.hour}$hourUnit ${localDateTime.minute}$minuteUnit"
                }
            }
        }
        val emptyDataSet = RangeBarDataSet(emptyEntries, "")
        data = RangeBarData(listOf(realDataSet, emptyDataSet))

        val minY = realDataSet.minEntry?.min
        val maxY = realDataSet.maxEntry?.max
        if (minY != null && maxY != null) {
            val adjustOffset = (maxY - minY) / 4
            axisLeft.apply {
                axisMinimum = minY - adjustOffset
                axisMaximum = maxY + adjustOffset
            }
        }
        invalidate()
    }

    private fun getBaseEntries(
        sleepStatistics: List<DailySleepStatistic>,
        valueGetter: (DailySleepStatistic) -> Float
    ): List<Pair<Float, Float?>> {
        val entries = mutableListOf<Pair<Float, Float?>>()
        val (startDate, endDate) = viewModel.selectedDays.value
        val duration = startDate.between(endDate)
        for (day in 0..duration.toDays()) {
            val recordDay = startDate.plusDays(day)
            val dailySleepStatistic = sleepStatistics.find { it.recordDay == recordDay }
            val targetValue = dailySleepStatistic?.let(valueGetter)
            entries.add(day.toFloat() to targetValue)
        }
        return entries
    }

    private fun BarLineChartBase<*>.setupXValueFormatter() {
        val startDate = viewModel.selectedDays.value.first
        val dateTimeFormatterPattern = when (viewModel.rangeDayType) {
            RangeDayType.WEEK -> "E\nMM/dd"
            RangeDayType.MONTH -> "d"
        }
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val localDateTime = startDate.plusDays(value.toLong())
                return DateTimeFormatter.ofPattern(dateTimeFormatterPattern).format(localDateTime)
            }
        }
    }

    private fun LocalDateTime.toLocalHour(): Float =
        toLocalTime().toSecondOfDay() / 3600f

    companion object : FragmentTemplate<DashboardFragment>() {
        private const val BAR_WIDTH = 0.5f

        fun newInstance(type: RangeDayType, sleepType: SleepType) =
            super.newInstance().apply {
                arguments = bundleOf(
                    DashboardViewModel.KEY_RANGE_DAY_TYPE to type,
                    DashboardViewModel.KEY_SLEEP_TYPE to sleepType,
                )
            }
    }
}
