package cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementYearly


@Composable
fun YearlyChart(measurements: List<MeasurementYearly>) {
    if (measurements.isEmpty()) {
        Text("Data nedostupná")
    } else {
        val sortedMeasurements = measurements.sortedBy { it.year }

        val measurementsWithoutLast = sortedMeasurements.dropLast(1)

        val entries = transformYearlyToEntries(measurementsWithoutLast)
        val labels = measurementsWithoutLast.map { it.year.toString() }

        LineChartComposable(entries, labels)
    }
}