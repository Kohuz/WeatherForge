package cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cz.cvut.weatherforge.core.utils.getLocalizedDateString
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import kotlinx.datetime.toJavaLocalDate


@Composable
fun DailyChart(measurements: List<MeasurementDaily>, history: Boolean? = false) {
    if (measurements.isEmpty()) {
        Text("Data nedostupná")
    } else {
        val sortedMeasurements = measurements.sortedBy { it.date }

        val entries = transformDailyToEntries(sortedMeasurements)
        var labels: List<String>
        if(history == true){
            labels = sortedMeasurements.map { it.date.year.toString()}

        }
        else {
            labels =
                sortedMeasurements.map { getLocalizedDateString(it.date.toJavaLocalDate()) }
        }

        LineChartComposable(entries, labels)
    }
}