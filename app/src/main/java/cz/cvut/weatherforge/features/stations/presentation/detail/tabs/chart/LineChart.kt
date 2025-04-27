package cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart


import android.content.Context
import android.graphics.Color
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementMonthly
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementYearly
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF

@Composable
fun LineChartComposable(entries: List<Entry>, labels: List<String>) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
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
                    setDrawCircleHole(false)
                    mode = LineDataSet.Mode.LINEAR
                    setDrawValues(true) // Enable value display
                    valueTextSize = 10f

                    // Disable circles for individual data points
                    setDrawCircles(false)

                    // Enable gradient fill
                    setDrawFilled(true)
                    fillDrawable = ContextCompat.getDrawable(context, R.drawable.chart_gradient)

                    // Custom fill formatter to fill only under the line
                    fillFormatter = object : IFillFormatter {
                        override fun getFillLinePosition(
                            dataSet: ILineDataSet,
                            dataProvider: LineDataProvider
                        ): Float {
                            // Return the minimum Y value of the chart (e.g., the bottom of the chart)
                            return (dataProvider as LineChart).axisLeft.axisMinimum
                        }
                    }

                    // Custom value formatter to conditionally display values based on zoom level
                    valueFormatter = object : ValueFormatter() {
                        override fun getPointLabel(entry: Entry?): String {
                            // Only show values if the chart is zoomed in enough
                            return if (scaleX > 2f) {
                                String.format("%.1f", entry?.y ?: 0f)
                            } else {
                                ""
                            }
                        }
                    }
                }
                val lineData = LineData(dataSet)
                data = lineData

                // Configure X-axis
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(true)
                xAxis.gridColor = Color.LTGRAY
                xAxis.gridLineWidth = 0.5f
                xAxis.textSize = 14f
                xAxis.textColor = Color.DKGRAY
                xAxis.setLabelRotationAngle(-45f)
                xAxis.setAvoidFirstLastClipping(true)
                xAxis.axisLineWidth = 1f
                xAxis.axisLineColor = Color.BLACK

                // Configure Y-axis
                axisLeft.isEnabled = true
                axisLeft.textSize = 14f
                axisLeft.textColor = Color.DKGRAY
                axisLeft.setDrawGridLines(true)
                axisLeft.gridColor = Color.LTGRAY
                axisLeft.gridLineWidth = 0.5f
                axisLeft.axisLineWidth = 1f
                axisLeft.axisLineColor = Color.BLACK
                axisRight.isEnabled = false

                legend.isEnabled = false

                val marker = ValueMarker(context, R.layout.marker_view, labels)
                marker.chartView = this
                this.marker = marker

                // Enable marker on click
                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                    }

                    override fun onNothingSelected() {
                    }
                })

                invalidate()
            }
        },
        update = { lineChart ->
            val dataSet = LineDataSet(entries, "Measurements").apply {
                color = Color.BLUE
                valueTextColor = Color.BLACK
                lineWidth = 2f
                setCircleColor(Color.RED)
                mode = LineDataSet.Mode.LINEAR
                setDrawValues(true)
                valueTextSize = 10f

                // Disable circles for individual data points
                setDrawCircles(false)

                // Enable gradient fill
                setDrawFilled(true)
                fillDrawable = ContextCompat.getDrawable(lineChart.context, R.drawable.chart_gradient)

                // Custom fill formatter to fill only under the line
                fillFormatter = object : IFillFormatter {
                    override fun getFillLinePosition(
                        dataSet: ILineDataSet,
                        dataProvider: LineDataProvider
                    ): Float {
                        // Return the minimum Y value of the chart (e.g., the bottom of the chart)
                        return (dataProvider as LineChart).axisLeft.axisMinimum
                    }
                }

                // Custom value formatter to conditionally display values based on zoom level
                valueFormatter = object : ValueFormatter() {
                    override fun getPointLabel(entry: Entry?): String {
                        // Only show values if the chart is zoomed in enough
                        return if (lineChart.scaleX > 2f) {
                            String.format("%.1f", entry?.y ?: 0f)
                        } else {
                            ""
                        }
                    }
                }
            }
            val lineData = LineData(dataSet)
            lineChart.data = lineData

            // Update X-axis labels
            lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)




            lineChart.invalidate()
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

class ValueMarker(
    context: Context,
    layoutResource: Int,
    private val labels: List<String>
) : MarkerView(context, layoutResource) {

    private val tvValue: TextView = findViewById(R.id.tvValue)

    // marker position to appear above the point
    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height - 10f)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val index = e?.x?.toInt() ?: 0
        val date = if (index < labels.size) labels[index] else ""
        val value = "%.1f".format(e?.y ?: 0f)
        tvValue.text = "$date: $value" // Show both date and value
        super.refreshContent(e, highlight)
    }
}