package ted.gun0912.sleep.ui.feature.deprecated.dashboard

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import ted.gun0912.sleep.ui.R

class XYMarkerView(context: Context, private val valueFormatter: ValueFormatter) :
    MarkerView(context, R.layout.view_chart_marker) {

    private val tvValue: TextView by lazy { findViewById(R.id.tv_value) }
    private val root: View by lazy { findViewById(R.id.root) }

    override fun refreshContent(e: Entry, highlight: Highlight) {
        root.isVisible = e.y > 0
        tvValue.text = valueFormatter.getFormattedValue(e.y)
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF =
        MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
}
