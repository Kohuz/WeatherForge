package cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily


@Composable
fun DailyChart(measurements: List<MeasurementDaily>) {
    if (measurements.isEmpty()) {
        Text("No data available")
    } else {
        // Sort measurements by date in ascending order
        val sortedMeasurements = measurements.sortedBy { it.date }

        // Transform sorted measurements to entries and labels
        val entries = transformDailyToEntries(sortedMeasurements)
        val labels = sortedMeasurements.map { it.date.toString() } // Use date as labels

        // Display the chart
        LineChartComposable(entries, labels)
    }
}