package ted.gun0912.sleep.ui.extension

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.feature.deprecated.dashboard.CustomXAxisRenderer
import ted.gun0912.sleep.ui.feature.deprecated.dashboard.XYMarkerView

const val BAR_WIDTH = 0.5f

fun BarLineChartBase<*>.setupChart(
    min: Float?,
    max: Float?,
    valueFormat: (Float) -> String,
    markerFormat: ((Float) -> String)? = null,
) {
    setPinchZoom(false)
    setScaleEnabled(false)
    setDrawGridBackground(false)
    setBackgroundColor(Color.TRANSPARENT)
    description.isEnabled = false
    axisRight.isEnabled = false
    legend.isEnabled = false

    xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        setDrawGridLines(false)
        textColor = context.getColor(R.color.chart_label)
        textSize = 12f
        axisMinimum = -0.5f
    }
    val xAxisRenderer = CustomXAxisRenderer(
        viewPortHandler,
        xAxis,
        getTransformer(YAxis.AxisDependency.LEFT)
    )
    setXAxisRenderer(xAxisRenderer)
    extraBottomOffset = 20f

    axisLeft.apply {
        setDrawGridLines(false)
        textColor = context.getColor(R.color.chart_label)
        textSize = 12f
        if (min != null) {
            axisMinimum = min
        }
        if (max != null) {
            axisMaximum = max
        }
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return valueFormat(value)
            }
        }
    }

    val xyMarkerView = XYMarkerView(context, object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val format = markerFormat ?: valueFormat
            return format(value)
        }
    })
    xyMarkerView.chartView = this
    marker = xyMarkerView
}

fun BarDataSet.setupColor(context: Context) {
    fun getColorInt(@ColorRes colorResId: Int): Int = ContextCompat.getColor(context, colorResId)
    color = getColorInt(R.color.colorPrimary)
    highLightColor = getColorInt(R.color.colorAccent)
    highLightAlpha = 255
}
