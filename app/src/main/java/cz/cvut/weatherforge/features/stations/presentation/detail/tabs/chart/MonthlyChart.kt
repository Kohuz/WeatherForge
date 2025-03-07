package cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart

import androidx.compose.runtime.Composable
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementMonthly


@Composable
fun MonthlyChart(measurements: List<MeasurementMonthly>) {
    val entries = transformMonthlyToEntries(measurements)
    val labels = measurements.map { it.month.toString() } // Use date as labels
    LineChartComposable(entries, labels)
}