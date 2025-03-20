package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import ResolutionDatePickerDialog
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
import cz.cvut.weatherforge.core.utils.getLocalizedDateString

import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreenViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.DailyChart
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.MonthlyChart
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.YearlyChart
import kotlinx.datetime.toJavaLocalDate
import java.time.LocalDate
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import cz.cvut.weatherforge.features.record.presentation.ElementDropdownMenu


@Composable
fun GraphContent(
    station: Station,
    detailScreenViewModel: DetailScreenViewModel,
    graphContentViewModel: GraphContentViewModel
) {
    val detailScreenState by detailScreenViewModel.screenStateStream.collectAsStateWithLifecycle()
    val graphContentState by graphContentViewModel.graphContentStateStream.collectAsStateWithLifecycle()

    val selectedResolution = graphContentState.selectedResolutionIndex
    val resolutions = listOf("Denně", "Měsíc a rok", "Ročně")

    // Show/hide date pickers
    if (graphContentState.showFromDatePicker) {
        ResolutionDatePickerDialog(
            minimumDate = station.stationElements
                .find { it.elementAbbreviation == graphContentState.selectedElement!!.abbreviation }
                ?.beginDate?.date?.toJavaLocalDate(),
            resolution = resolutions[selectedResolution],
            onDismiss = { graphContentViewModel.showFromDatePicker(false) },
            onDateSelected = { date ->
                graphContentViewModel.setFromDate(date)
            },
            dateToShow = LocalDate.now().minusMonths(3)
        )
    }

    if (graphContentState.showToDatePicker) {
        ResolutionDatePickerDialog(
            minimumDate = graphContentState.fromDate,
            resolution = resolutions[selectedResolution],
            onDismiss = { graphContentViewModel.showToDatePicker(false) },
            onDateSelected = { date ->
                graphContentViewModel.setToDate(date)
            },
            dateToShow = LocalDate.now().minusMonths(1)
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
                "Měsíc a rok" -> detailScreenViewModel.fetchMonthlyMeasurements(
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
        ElementDropdownMenu(
            items = detailScreenState.elementCodelist,
            selectedItem = graphContentState.selectedElement,
            onItemSelected = { element ->
                graphContentViewModel.selectElement(element)
            }
        )

        // Show date selectors only if an element is selected
        if (graphContentState.selectedElement != null) {
            // Date Selectors for fromDate and toDate
            val beginDate = station.stationElements
                .find { it.elementAbbreviation == graphContentState.selectedElement!!.abbreviation }
                ?.beginDate

            if (beginDate != null) {
                Text(
                    text = "${stringResource(R.string.detail_measurement_started_on)}: ${getLocalizedDateString(beginDate.date.toJavaLocalDate())}",
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
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.medium, // Rounded corners
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = graphContentState.fromDate?.toString() ?: "Select From Date",
                        modifier = Modifier.padding(8.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // To Date Selector
            OutlinedButton(
                onClick = { graphContentViewModel.showToDatePicker(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.medium, // Rounded corners
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = graphContentState.toDate?.toString() ?: "Select To Date",
                        modifier = Modifier.padding(8.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.size(24.dp)
                    )
                }
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
                    )
                }
            }
        }

        // Display the chart
        if (graphContentState.selectedElement != null && graphContentState.fromDate != null && graphContentState.toDate != null) {
            when (resolutions[selectedResolution]) {
                "Denně" -> DailyChart(detailScreenState.dailyMeasurements)
                "Měsíc a rok" -> MonthlyChart(detailScreenState.monthlyMeasurements)
                "Ročně" -> YearlyChart(detailScreenState.yearlyMeasurements)
            }
        }
    }
}


