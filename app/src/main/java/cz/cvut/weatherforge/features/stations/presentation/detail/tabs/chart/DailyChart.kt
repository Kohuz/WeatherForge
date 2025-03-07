package cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart

import androidx.compose.runtime.Composable
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily


@Composable
fun DailyChart(measurements: List<MeasurementDaily>) {
    val entries = transformDailyToEntries(measurements)
    val labels = measurements.map { it.date.toString() } // Use date as labels
    LineChartComposable(entries, labels)
}