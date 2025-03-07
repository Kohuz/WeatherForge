package cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart

import androidx.compose.runtime.Composable
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementYearly


@Composable
fun YearlyChart(measurements: List<MeasurementYearly>) {
    val entries = transformYearlyToEntries(measurements)
    val labels = measurements.map { it.year.toString() } // Use date as labels
    LineChartComposable(entries, labels)
}