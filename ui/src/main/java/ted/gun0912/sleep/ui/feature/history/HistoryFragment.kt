package ted.gun0912.sleep.ui.feature.history

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import dagger.hilt.android.AndroidEntryPoint
import ted.gun0912.rangebarchart.RangeBarChart
import ted.gun0912.rangebarchart.RangeBarData
import ted.gun0912.rangebarchart.RangeBarDataSet
import ted.gun0912.rangebarchart.RangeBarEntry
import ted.gun0912.sleep.model.HourlySleepStatistic
import ted.gun0912.sleep.model.SleepType
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.databinding.FragmentHistoryBinding
import ted.gun0912.sleep.ui.extension.BAR_WIDTH
import ted.gun0912.sleep.ui.extension.getColorInt
import ted.gun0912.sleep.ui.extension.setupChart
import ted.gun0912.sleep.ui.extension.setupColor
import ted.gun0912.sleep.ui.feature.history.HistoryViewModel.Event
import ted.gun0912.sleep.ui.feature.main.BaseTabFragment
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class HistoryFragment :
    BaseTabFragment<FragmentHistoryBinding, HistoryViewModel, Event>(R.layout.fragment_history) {
    override val viewModel: HistoryViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        setupChart()

        viewModel.hourlySleepStatistics
            .observe(::updateStatistic)

        viewModel.recordDays
            .observe {
                setupCalendar(it.toCalendarData())
            }
        setupSleepTypeListener()
    }

    override fun onResume() {//YJH0711 추가
        super.onResume()
        /*viewModel.recordDays
            .observe {
                setupCalendar(it.toCalendarData())
            }*/
    }

    override fun handleEvent(event: Event) = when (event) {
        is Event.RawData -> sendRawData(event.SleepRecord)
        else -> {}
    }

    private fun setupListener() {
        binding.tvSelectedDate.setOnClickListener {
            with(binding.calendarLayout) {
                if (isExpand) {
                    shrink()
                } else {
                    expand()
                }
            }
        }
    }

    private fun sendRawData(rawData: String) {
        val selectedData: LocalDate = viewModel.selectedDate!!
        val sleepType: SleepType = viewModel.sleepType
        val fileName = sleepType.toString() + selectedData.toString()

        val dirPath = context!!.filesDir
        val file = File(dirPath, "$fileName.txt")

        file.writeText(rawData)

        val rawDataURI =
            FileProvider.getUriForFile(context!!, context!!.packageName + ".provider", file)

        ShareCompat.IntentBuilder(context!!)
            .setType("application/*")
            .setStream(rawDataURI)
            .setChooserTitle("Share")
            .startChooser()
    }

    private fun setupSleepTypeListener() {
        binding.dropdownSleepType.setOnItemClickListener { _, _, position, _ ->
            val sleepType = SleepType.values()[position]
            viewModel.changeSleepType(sleepType)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun List<LocalDate>.toCalendarData(): Map<String, Calendar> =
        associate { recordDay ->
            val calendar = Calendar().apply {
                this.year = recordDay.year
                this.month = recordDay.monthValue
                this.day = recordDay.dayOfMonth
            }
            calendar.toString() to calendar
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCalendar(historyDates: Map<String, Calendar>) = with(binding.calendarView) {
        if (historyDates.isEmpty()) {
            isVisible = false
            return@with
        }
        setOnCalendarInterceptListener(object : CalendarView.OnCalendarInterceptListener {
            override fun onCalendarIntercept(calendar: Calendar): Boolean =
                !historyDates.containsKey(calendar.toString())

            override fun onCalendarInterceptClick(calendar: Calendar, isClick: Boolean) = Unit
        })
        setOnCalendarSelectListener(object :
            CalendarView.OnCalendarSelectListener {
            override fun onCalendarOutOfRange(calendar: Calendar) = Unit

            @RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
                val localDate = LocalDate.of(calendar.year, calendar.month, calendar.day)
                binding.tvSelectedDate.text =
                    DateTimeFormatter.ofPattern("yyyy/MM/dd").format(localDate)
                if (isClick) {
                    viewModel.fetchDate(localDate)
                }
            }
        })
        setOnViewChangeListener { binding.isCalendarExpanded = it }

        setSchemeDate(historyDates)
        val firstCalendar = historyDates.values.first()
        val lastCalendar = historyDates.values.last()
        setRange(
            firstCalendar.year, firstCalendar.month, firstCalendar.day,
            lastCalendar.year, lastCalendar.month, lastCalendar.day
        )
        scrollToCalendar(lastCalendar.year, lastCalendar.month, lastCalendar.day)
        val lastLocalDate = LocalDate.of(lastCalendar.year, lastCalendar.month, lastCalendar.day)
        viewModel.fetchDate(lastLocalDate)
        isVisible = true
    }


    private fun setupChart() = with(binding) {
        chartHeart.setupChart(50f, 120f, { "${it.toInt()}" }, { "${it.toInt()}bpm" })
        chartRespiration.setupChart(10f, 30f, { "${it.toInt()}" }, { "${it.toInt()}rpm" })
        chartSleepMoving.setupChart(null, null, { "${it.toInt()}${getString(R.string.count)}" })
    }

    private fun updateStatistic(sleepStatistics: List<HourlySleepStatistic>) = with(binding) {
        if (sleepStatistics.isEmpty()) {
            return@with
        }
        chartHeart.updateData(sleepStatistics) { it.minHeartRate.toFloat() to it.maxHeartRate.toFloat() }
        chartRespiration.updateData(sleepStatistics) { it.minRespirationRate.toFloat() to it.maxRespirationRate.toFloat() }
        chartSleepMoving.updateSleepMovingData(sleepStatistics)

    }

    private fun BarChart.updateSleepMovingData(sleepStatistics: List<HourlySleepStatistic>) {
        val normalEntries = mutableListOf<BarEntry>()
        val highEntries = mutableListOf<BarEntry>()
        val sortedSleepStatistics = sleepStatistics.sortedByDescending { it.moving }.take(2)
        sleepStatistics.forEachIndexed { index, hourlySleepStatistic ->
            val x = index.toFloat()
            val y = hourlySleepStatistic.moving.toFloat()
            Log.d("Test",hourlySleepStatistic.toString())

            val entry = BarEntry(x, y)
            /*if (sortedSleepStatistics.contains(hourlySleepStatistic)) {
                highEntries.add(entry)
            } else {
                normalEntries.add(entry)
            }*/
            normalEntries.add(entry) // YJH0816
        }
        val normalDataSet = BarDataSet(normalEntries, null).apply {
            setupColor(requireContext())
        }
        val highDataSet = BarDataSet(highEntries, null).apply {
            setupColor(requireContext())
            color = getColorInt(R.color.colorAccent)
        }
        val barData = BarData(listOf(normalDataSet, highDataSet)).apply {
            barWidth = BAR_WIDTH
        }
        data = barData
        setupXValueFormatter(sleepStatistics.first().recordDateTime)
        invalidate()
    }

    private fun RangeBarChart.updateData(
        sleepStatistics: List<HourlySleepStatistic>,
        valueGetter: (HourlySleepStatistic) -> Pair<Float, Float>
    ) {
        val entries = sleepStatistics.mapIndexed { index, hourlySleepStatistic ->
            val x = index.toFloat()
            val (min, max) = valueGetter(hourlySleepStatistic)
            RangeBarEntry(x, min, max)
        }
        setupXValueFormatter(sleepStatistics.first().recordDateTime)
        val dataSet = RangeBarDataSet(entries, "RangeBar").apply {
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
                    return "${value.toInt()}"
                }
            }
        }
        data = RangeBarData(listOf(dataSet))

        val minY = dataSet.minEntry?.min
        val maxY = dataSet.maxEntry?.max
        if (minY != null && maxY != null) {
            val adjustOffset = (maxY - minY) / 4
            axisLeft.apply {
                axisMinimum = minY - adjustOffset
                axisMaximum = maxY + adjustOffset
            }
        }
        invalidate()
    }

    private fun BarLineChartBase<*>.setupXValueFormatter(startDateTime: LocalDateTime) {
        xAxis.valueFormatter = object : ValueFormatter() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun getFormattedValue(value: Float): String {
                val localDateTime = startDateTime.plusMinutes(30 * value.toLong())
                val hourUnit = getString(R.string.hour)
                val hour = DateTimeFormatter.ofPattern("hh")
                    .format(localDateTime)
                return "$hour$hourUnit"
            }
        }
    }
}

