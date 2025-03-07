package cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart


import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementMonthly
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementYearly

@Composable
fun LineChartComposable(entries: List<Entry>, labels: List<String>) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                // Configure the chart
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)

                // Set data
                val dataSet = LineDataSet(entries, "Measurements").apply {
                    color = Color.BLUE
                    valueTextColor = Color.BLACK
                    lineWidth = 2f
                }
                val lineData = LineData(dataSet)
                data = lineData

                // Set X-axis labels
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)

                // Refresh the chart
                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

fun transformDailyToEntries(measurements: List<MeasurementDaily>): List<Entry> {
    return measurements.mapIndexed { index, measurement ->
        Entry(
            index.toFloat(), // X-axis: Index of the day
            measurement.value?.toFloat() ?: 0f // Y-axis: Daily value (or 0 if null)
        )
    }
}

fun transformMonthlyToEntries(measurements: List<MeasurementMonthly>): List<Entry> {
    return measurements.mapIndexed { index, measurement ->
        Entry(
            index.toFloat(), // X-axis: Index of the month
            measurement.value?.toFloat() ?: 0f // Y-axis: Monthly value (or 0 if null)
        )
    }
}

fun transformYearlyToEntries(measurements: List<MeasurementYearly>): List<Entry> {
    return measurements.mapIndexed { index, measurement ->
        Entry(
            index.toFloat(), // X-axis: Index of the year
            measurement.value?.toFloat() ?: 0f // Y-axis: Yearly value (or 0 if null)
        )
    }
}