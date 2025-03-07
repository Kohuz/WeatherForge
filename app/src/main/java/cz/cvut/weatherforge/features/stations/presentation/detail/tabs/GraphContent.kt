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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreenViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.DailyChart
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.MonthlyChart
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.YearlyChart

@Composable
fun GraphContent(station: Station,
                 detailScreenViewModel: DetailScreenViewModel,
                 graphContentViewModel: GraphContentViewModel) {
    val detailScreenState by detailScreenViewModel.screenStateStream.collectAsStateWithLifecycle()
    val graphContentState by graphContentViewModel.graphContentStateStream.collectAsStateWithLifecycle()

    val selectedResolution = graphContentState.selectedResolutionIndex
    val resolutions = listOf("Denně", "Měsíčně", "Ročně")

    // Show/hide date pickers
    if (graphContentState.showFromDatePicker) {
        DatePickerDialog(
            resolution = resolutions[selectedResolution],
            onDismiss = { graphContentViewModel.showFromDatePicker(false) },
            onDateSelected = { date -> graphContentViewModel.setFromDate(date) }
        )
    }

    if (graphContentState.showToDatePicker) {
        DatePickerDialog(
            resolution = resolutions[selectedResolution],
            onDismiss = { graphContentViewModel.showToDatePicker(false) },
            onDateSelected = { date -> graphContentViewModel.setToDate(date) }
        )
    }

    LaunchedEffect(selectedResolution) {
        if(graphContentState.selectedElement != null && graphContentState.fromDate != null && graphContentState.toDate != null) {
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

    // Display the chart
    when (resolutions[selectedResolution]) {
        "Denně" -> DailyChart(detailScreenState.dailyMeasurements)
        "Měsíčně" -> MonthlyChart(detailScreenState.monthlyMeasurements)
        "Ročně" -> YearlyChart(detailScreenState.yearlyMeasurements)
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
                    text = graphContentState.selectedElement?.name ?: "Select Station Element",
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
                            graphContentViewModel.toggleDropdown(false)
                        },
                        text = {
                            Text(text = element.name)
                        }
                    )
                }
            }
        }

        // Date Selectors for fromDate and toDate
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // From Date Selector
            OutlinedButton(
                onClick = { graphContentViewModel.showFromDatePicker(true) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(text = if (graphContentState.fromDate != null) graphContentState.fromDate.toString() else "Select From Date")
            }

            // To Date Selector
            OutlinedButton(
                onClick = { graphContentViewModel.showToDatePicker(true) },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(text = if (graphContentState.toDate != null) graphContentState.toDate.toString() else "Select To Date")
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
    }
}