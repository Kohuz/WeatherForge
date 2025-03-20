package cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart


import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
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
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawValues(false)
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
                xAxis.textSize = 14f // Increase text size for better visibility
                xAxis.textColor = Color.DKGRAY // Use a darker color for better visibility
                xAxis.setLabelRotationAngle(-45f)
                xAxis.setAvoidFirstLastClipping(true)
                xAxis.axisLineWidth = 1f // Increase axis line width
                xAxis.axisLineColor = Color.BLACK // Darker axis line color

                // Configure Y-axis
                axisLeft.isEnabled = true
                axisLeft.textSize = 14f // Increase text size for better visibility
                axisLeft.textColor = Color.DKGRAY // Use a darker color for better visibility
                axisLeft.setDrawGridLines(true)
                axisLeft.gridColor = Color.LTGRAY
                axisLeft.gridLineWidth = 0.5f
                axisLeft.axisLineWidth = 1f // Increase axis line width
                axisLeft.axisLineColor = Color.BLACK // Darker axis line color
                axisRight.isEnabled = false

                legend.isEnabled = false

                // Refresh the chart
                invalidate()
            }
        },
        update = { lineChart ->
            // Update the chart data
            val dataSet = LineDataSet(entries, "Measurements").apply {
                color = Color.BLUE
                valueTextColor = Color.BLACK
                lineWidth = 2f
                setCircleColor(Color.RED)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawValues(false)
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
            }
            val lineData = LineData(dataSet)
            lineChart.data = lineData

            // Update X-axis labels
            lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

            // Refresh the chart
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