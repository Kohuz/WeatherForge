package cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cz.cvut.weatherforge.core.utils.getLocalizedDateString
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementMonthly
import kotlinx.datetime.toJavaLocalDate


@Composable
fun MonthlyChart(measurements: List<MeasurementMonthly>, history: Boolean? = false) {
    if (measurements.isEmpty()) {
        Text("Data nedostupná")
    } else {
        val sortedMeasurements = measurements.sortedWith(compareBy({ it.year }, { it.month }))
        val measurementsWithoutLast = sortedMeasurements.dropLast(1)
        val entries = transformMonthlyToEntries(measurementsWithoutLast)
        var labels: List<String>
        if(history == true) {
            labels = measurementsWithoutLast.map { it.year.toString() }
        }
        else {
            labels = measurementsWithoutLast.map { it.month.toString() }
        }

        LineChartComposable(entries, labels)
    }
}