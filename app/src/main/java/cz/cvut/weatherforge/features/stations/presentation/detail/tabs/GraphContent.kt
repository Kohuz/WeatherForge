package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import ResolutionDatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.core.utils.getLocalizedDateString
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.data.model.StationElement
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreenViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.DailyChart
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.MonthlyChart
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.YearlyChart
import kotlinx.datetime.toJavaLocalDate
import java.time.LocalDate


@OptIn(ExperimentalLayoutApi::class)
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
                .find { it.elementAbbreviation == graphContentState.selectedElement!!.elementAbbreviation }
                ?.beginDate?.date?.toJavaLocalDate(),
            resolution = resolutions[selectedResolution],
            onDismiss = { graphContentViewModel.showFromDatePicker(false) },
            onDateSelected = { date ->
                graphContentViewModel.setFromDate(date)
            },
            dateToShow = graphContentState.fromDate ?: LocalDate.now().minusMonths(3)
        )
    }

    if (graphContentState.showToDatePicker && graphContentState.fromDate != null) {
        ResolutionDatePickerDialog(
            minimumDate = graphContentState.fromDate,
            resolution = resolutions[selectedResolution],
            onDismiss = { graphContentViewModel.showToDatePicker(false) },
            onDateSelected = { date ->
                graphContentViewModel.setToDate(date)
            },
            dateToShow = graphContentState.fromDate?.let { graphContentState.fromDate?.let { md -> maxOf(it, md) } ?: it }
                ?:  graphContentState.fromDate?.let { maxOf(LocalDate.now().minusMonths(3), it) } ?: LocalDate.now().minusMonths(3)        )
    }

    // Fetch data when all parameters are available
    LaunchedEffect(selectedResolution, graphContentState.fromDate, graphContentState.toDate, graphContentState.selectedElement) {
        if (graphContentState.selectedElement != null && graphContentState.fromDate != null && graphContentState.toDate != null) {
                when (resolutions[selectedResolution]) {
                    "Denně" -> detailScreenViewModel.fetchDailyMeasurements(
                        station.stationId,
                        graphContentState.fromDate.toString(),
                        graphContentState.toDate.toString(),
                        graphContentState.selectedElement!!.elementAbbreviation
                    )

                    "Měsíc a rok" -> detailScreenViewModel.fetchMonthlyMeasurements(
                        station.stationId,
                        graphContentState.fromDate.toString(),
                        graphContentState.toDate.toString(),
                        graphContentState.selectedElement!!.elementAbbreviation
                    )

                    "Ročně" -> detailScreenViewModel.fetchYearlyMeasurements(
                        station.stationId,
                        graphContentState.fromDate.toString(),
                        graphContentState.toDate.toString(),
                        graphContentState.selectedElement!!.elementAbbreviation
                    )
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        detailScreenState.station?.let {
            StationElementDropdown(
                items = it.stationElements,
                selectedItem = graphContentState.selectedElement,
                onItemSelected = { element ->
                    graphContentViewModel.selectElement(element)
                }
            )
        }

        // Radio buttons for selecting resolution
        FlowRow (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
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
                        resolution,
                        maxLines = 2,
                        softWrap = true,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(80.dp)
                    )
                }
            }
        }


        // Show date selectors only if an element is selected
        if (graphContentState.selectedElement != null) {
            // Date Selectors for fromDate and toDate
            val beginDate = station.stationElements
                .find { it.elementAbbreviation == graphContentState.selectedElement!!.elementAbbreviation }
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
                        text = graphContentState.fromDate?.formatForResolution(resolutions[selectedResolution])
                            ?: stringResource(R.string.select_from_date),
                        modifier = Modifier.padding(8.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            OutlinedButton(
                onClick = { graphContentViewModel.showToDatePicker(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.medium,
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
                        text = graphContentState.toDate?.formatForResolution(resolutions[selectedResolution])
                            ?: stringResource(R.string.select_to_date),
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


        // Display the chart
        if (graphContentState.selectedElement != null && graphContentState.fromDate != null && graphContentState.toDate != null) {

            if(!detailScreenState.graphLoading){
                when (resolutions[selectedResolution]) {
                "Denně" -> DailyChart(detailScreenState.dailyMeasurements)
                "Měsíc a rok" -> MonthlyChart(detailScreenState.monthlyMeasurements)
                "Ročně" -> YearlyChart(detailScreenState.yearlyMeasurements)
                }
            } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

            }
        }
}


@Composable
fun StationElementDropdown(
    items: List<StationElement>,
    selectedItem: StationElement?,
    onItemSelected: (StationElement) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // Internal state for dropdown visibility

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Enhanced Button to toggle dropdown visibility
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
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
                    text = selectedItem?.elementName ?: stringResource(R.string.select_element),
                    modifier = Modifier.padding(8.dp)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false // Close dropdown after selection
                    },
                    text = {
                        Text(text = item.elementName)
                    }
                )
            }
        }
    }
}

fun LocalDate.formatForResolution(resolution: String): String {
    return when (resolution) {
        "Denně" -> getLocalizedDateString(this)
        "Den a měsíc" -> "${this.dayOfMonth}. ${this.monthValue}."
        "Měsíc a rok" -> "${this.monthValue}/${this.year}"
        "Ročně" -> this.year.toString()
        else -> this.toString()
    }
}