package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import DatePickerDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementMonthly
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementYearly
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreenViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.DailyChart
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.MonthlyChart
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.YearlyChart
import kotlinx.datetime.toJavaLocalDate


@Composable
fun GraphContent(
    station: Station,
    detailScreenViewModel: DetailScreenViewModel,
    graphContentViewModel: GraphContentViewModel
) {
    val detailScreenState by detailScreenViewModel.screenStateStream.collectAsStateWithLifecycle()
    val graphContentState by graphContentViewModel.graphContentStateStream.collectAsStateWithLifecycle()

    val selectedResolution = graphContentState.selectedResolutionIndex
    val resolutions = listOf("Denně", "Měsíčně", "Ročně")
    val aggregationTypes = listOf("MIN", "MAX", "AVG")
    val selectedAggregationType = graphContentState.selectedAggregationType

    // Show/hide date pickers
    if (graphContentState.showFromDatePicker) {
        DatePickerDialog(
            minimumDate = station.stationElements
                .find { it.elementAbbreviation == graphContentState.selectedElement!!.abbreviation }
                ?.beginDate?.date?.toJavaLocalDate(),
            resolution = resolutions[selectedResolution],
            onDismiss = { graphContentViewModel.showFromDatePicker(false) },
            onDateSelected = { date ->
                graphContentViewModel.setFromDate(date)
            }
        )
    }

    if (graphContentState.showToDatePicker) {
        DatePickerDialog(
            minimumDate = graphContentState.fromDate, // Ensure toDate is after fromDate
            resolution = resolutions[selectedResolution],
            onDismiss = { graphContentViewModel.showToDatePicker(false) },
            onDateSelected = { date ->
                graphContentViewModel.setToDate(date)
            }
        )
    }

    // Fetch data when all parameters are available
    LaunchedEffect(selectedResolution, graphContentState.fromDate, graphContentState.toDate, graphContentState.selectedElement) {
        if (graphContentState.selectedElement != null && graphContentState.fromDate != null && graphContentState.toDate != null) {
            when (resolutions[selectedResolution]) {
                "Denně" -> detailScreenViewModel.fetchDailyMeasurements(
                    station.stationId,
                    graphContentState.fromDate.toString(),
                    graphContentState.toDate.toString(),
                    graphContentState.selectedElement!!.abbreviation
                )
                "Měsíčně" -> detailScreenViewModel.fetchMonthlyMeasurements(
                    station.stationId,
                    graphContentState.fromDate.toString(),
                    graphContentState.toDate.toString(),
                    graphContentState.selectedElement!!.abbreviation
                )
                "Ročně" -> detailScreenViewModel.fetchYearlyMeasurements(
                    station.stationId,
                    graphContentState.fromDate.toString(),
                    graphContentState.toDate.toString(),
                    graphContentState.selectedElement!!.abbreviation
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Dropdown for selecting station elements
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // Button to toggle dropdown visibility
            OutlinedButton(
                onClick = { graphContentViewModel.toggleDropdown(!graphContentState.expanded) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = graphContentState.selectedElement?.name ?: stringResource(R.string.detail_select_station_element),
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Dropdown menu
            DropdownMenu(
                expanded = graphContentState.expanded,
                onDismissRequest = { graphContentViewModel.toggleDropdown(false) },
                modifier = Modifier.fillMaxWidth()
            ) {
                detailScreenState.elementCodelist.forEach { element ->
                    DropdownMenuItem(
                        onClick = {
                            graphContentViewModel.selectElement(element)
                            // Set the fromDate to the beginDate of the selected element
                            val beginDate = station.stationElements
                                .find { it.elementAbbreviation == element.abbreviation }
                                ?.beginDate
                            if (beginDate != null) {
                                graphContentViewModel.setFromDate(beginDate.date.toJavaLocalDate())
                            }
                            graphContentViewModel.toggleDropdown(false)
                        },
                        text = {
                            Text(text = element.name)
                        }
                    )
                }
            }
        }

        // Show date selectors only if an element is selected
        if (graphContentState.selectedElement != null) {
            // Date Selectors for fromDate and toDate
            val beginDate = station.stationElements
                .find { it.elementAbbreviation == graphContentState.selectedElement!!.abbreviation }
                ?.beginDate

            if (beginDate != null) {
                Text(
                    text = "${stringResource(R.string.detail_measurement_started_on)}: ${beginDate.date}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            // From Date Selector
            OutlinedButton(
                onClick = { graphContentViewModel.showFromDatePicker(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = graphContentState.fromDate?.toString() ?: "Select From Date"
                )
            }

            // To Date Selector
            OutlinedButton(
                onClick = { graphContentViewModel.showToDatePicker(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = graphContentState.toDate?.toString() ?: "Select To Date"
                )
            }
        }

        // Radio buttons for selecting resolution
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            resolutions.forEachIndexed { index, resolution ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = (index == selectedResolution),
                            onClick = { graphContentViewModel.selectResolution(index) },
                            role = Role.RadioButton
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (index == selectedResolution),
                        onClick = { graphContentViewModel.selectResolution(index) }
                    )
                    Text(
                        text = resolution,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        if (resolutions[selectedResolution] == "Měsíčně" || resolutions[selectedResolution] == "Ročně") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                aggregationTypes.forEach { type ->
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = (type == selectedAggregationType),
                                onClick = { graphContentViewModel.selectAggregationType(type) },
                                role = Role.RadioButton
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (type == selectedAggregationType),
                            onClick = { graphContentViewModel.selectAggregationType(type) }
                        )
                        Text(
                            text = type,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        // Display the chart
        if (graphContentState.selectedElement != null && graphContentState.fromDate != null && graphContentState.toDate != null) {
            when (resolutions[selectedResolution]) {
                "Denně" -> DailyChart(detailScreenState.dailyMeasurements)
                "Měsíčně" -> {
                    val filteredMeasurements = filterMeasurementsMonthly(
                        detailScreenState.monthlyMeasurements,
                        graphContentState.selectedAggregationType
                    )
                    MonthlyChart(filteredMeasurements)
                }
                "Ročně" -> {
                    val filteredMeasurements = filterMeasurementsYearly(
                        detailScreenState.yearlyMeasurements,
                        graphContentState.selectedAggregationType
                    )
                    YearlyChart(filteredMeasurements)
                }
            }
        }
    }
}


private fun filterMeasurementsMonthly(
    measurements: List<MeasurementMonthly>,
    aggregationType: String
): List<MeasurementMonthly> {
    return measurements.filter { measurement ->
        measurement.timeFunction == "AVG" && measurement.mdFunction == aggregationType
    }
}

private fun filterMeasurementsYearly(
    measurements: List<MeasurementYearly>,
    aggregationType: String
): List<MeasurementYearly> {
    return measurements.filter { measurement ->
        measurement.timeFunction == "AVG" && measurement.mdFunction == aggregationType
    }
}