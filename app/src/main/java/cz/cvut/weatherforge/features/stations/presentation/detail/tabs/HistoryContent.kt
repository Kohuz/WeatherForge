package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import ResolutionDatePickerDialog
import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreenViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.elementAbbreviationToNameUnitPair
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.DailyChart
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.MonthlyChart
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate


@Composable
fun HistoryContent(
    stationId: String,
    historyContentViewModel: HistoryContentViewModel,
    detailViewModel: DetailScreenViewModel
) {
    val historyContentState by historyContentViewModel.historyContentState.collectAsStateWithLifecycle()
    val detailState by detailViewModel.screenStateStream.collectAsStateWithLifecycle()
    val resolutions = listOf("Den a měsíc", "Měsíčně")
    val selectedResolution = historyContentState.selectedResolutionIndex

    LaunchedEffect(historyContentState.selectedConcreteDayDate) {
        historyContentViewModel.fetchConcreteDayData(stationId)
    }

    LaunchedEffect(historyContentState.selectedLongTermDate) {
        historyContentViewModel.fetchLongTermStats(stationId)
    }

    LaunchedEffect(historyContentState.selectedGraphDate, historyContentState.selectedElement) {
        when (resolutions[selectedResolution]) {
            "Denně" -> {
                historyContentViewModel.fetchDailyMeasurements(
                    stationId,
                    historyContentState.selectedElement!!.abbreviation,
                    historyContentState.selectedGraphDate!!
                )

            }

            "Měsíčně" -> {
                historyContentViewModel.fetchMonthlyMeasurements(
                    stationId,
                    historyContentState.selectedElement!!.abbreviation,
                    historyContentState.selectedGraphDate!!
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedButton(
            onClick = { historyContentViewModel.showLongTermDatePicker(true) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.select_day_month,
                        historyContentState.selectedLongTermDate?.toString() ?: stringResource(R.string.no_date_selected)
                    )
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.select_date),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (historyContentState.showLongTermDatePicker) {
            ResolutionDatePickerDialog(
                minimumDate = historyContentState.selectedLongTermDate?.toJavaLocalDate(),
                resolution = "Den a měsíc",
                onDismiss = { historyContentViewModel.showLongTermDatePicker(false) },
                onDateSelected = { date -> historyContentViewModel.setSelectedLongTermDate(date.toKotlinLocalDate()) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display error message
        if (historyContentState.error != null) {
            Text(
                text = stringResource(R.string.error_message, historyContentState.error ?: ""),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Display data in Cards
        if (historyContentState.dailyStats != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.weather_statistics),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.temperature),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(stringResource(R.string.min_temperature, historyContentState.dailyStats!!.valueStats.find { it.element == "TMI" }?.lowest ?: "--"))
                            Text(stringResource(R.string.max_temperature, historyContentState.dailyStats!!.valueStats.find { it.element == "TMA" }?.highest ?: "--"))
                            Text(stringResource(R.string.avg_temperature, historyContentState.dailyStats!!.valueStats.find { it.element == "T" }?.average?.let { String.format("%.1f", it) } ?: "--"))
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.precipitation),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(stringResource(R.string.max_precipitation, historyContentState.dailyStats!!.valueStats.find { it.element == "SVH" }?.highest ?: "--"))
                            Text(stringResource(R.string.avg_precipitation, historyContentState.dailyStats!!.valueStats.find { it.element == "SVH" }?.average?.let { String.format("%.1f", it) } ?: "--"))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.wind),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(stringResource(R.string.max_wind, historyContentState.dailyStats!!.valueStats.find { it.element == "FMAX" }?.highest ?: "--"))
                            Text(stringResource(R.string.avg_wind, historyContentState.dailyStats!!.valueStats.find { it.element == "F" }?.average?.let { String.format("%.1f", it) } ?: "--"))
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.snow),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(stringResource(R.string.max_snow, historyContentState.dailyStats!!.valueStats.find { it.element == "SCE" }?.highest ?: "--"))
                            Text(stringResource(R.string.max_new_snow, historyContentState.dailyStats!!.valueStats.find { it.element == "SNO" }?.highest?.let { String.format("%.1f", it) } ?: "--"))
                            Text(stringResource(R.string.avg_snow, historyContentState.dailyStats!!.valueStats.find { it.element == "SCE" }?.average?.let { String.format("%.1f", it) } ?: "--"))
                            Text(stringResource(R.string.avg_new_snow, historyContentState.dailyStats!!.valueStats.find { it.element == "SNO" }?.average?.let { String.format("%.1f", it) } ?: "--"))
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        OutlinedButton(
            onClick = { historyContentViewModel.showConcreteDatePicker(true) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.select_date,
                        historyContentState.selectedConcreteDayDate?.toString() ?: stringResource(R.string.no_date_selected)
                    )
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.select_date),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (historyContentState.showConcreteDayDatePicker) {
            ResolutionDatePickerDialog(
                minimumDate = detailState.station?.startDate?.date?.toJavaLocalDate(),
                resolution = "Denně",
                onDismiss = { historyContentViewModel.showConcreteDatePicker(false) },
                onDateSelected = { date -> historyContentViewModel.setSelectedConcreteDayDate(date.toKotlinLocalDate()) }
            )
        }
        if (historyContentState.statsDay != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.daily_stats),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    historyContentState.statsDay!!.measurements.forEach { measurement ->
                        val nameUnitPair = elementAbbreviationToNameUnitPair(measurement.element, detailState.elementCodelist)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (nameUnitPair != null) {
                                Text(
                                    text = nameUnitPair.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Text(
                                text = "${measurement.value.toString()} ${nameUnitPair?.unit}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // Resolution Radio Buttons
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
                            onClick = { historyContentViewModel.selectResolution(index) },
                            role = Role.RadioButton
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (index == selectedResolution),
                        onClick = { historyContentViewModel.selectResolution(index) }
                    )
                    Text(
                        text = resolution,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        OutlinedButton(
            onClick = { historyContentViewModel.toggleDropdown(!historyContentState.dropdownExpanded) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium, // Rounded corners
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = historyContentState.selectedElement?.name ?: stringResource(R.string.detail_select_station_element),
                    modifier = Modifier.padding(8.dp)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.select_element),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        DropdownMenu(
            expanded = historyContentState.dropdownExpanded,
            onDismissRequest = { historyContentViewModel.toggleDropdown(false) },
            modifier = Modifier.fillMaxWidth()
        ) {
            detailState.elementCodelist.forEach { element ->
                DropdownMenuItem(
                    onClick = {
                        historyContentViewModel.selectElement(element)
                        historyContentViewModel.toggleDropdown(false)
                    },
                    text = {
                        Text(text = element.name)
                    }
                )
            }
        }





        OutlinedButton(
            onClick = { historyContentViewModel.showGraphDatePicker(true) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.select_day_month,
                        historyContentState.selectedGraphDate?.toString() ?: stringResource(R.string.no_date_selected)
                    )
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.select_date),
                    modifier = Modifier.size(24.dp)
                )
            }
        }


        if (historyContentState.showGraphDatePicker) {
            ResolutionDatePickerDialog(
                minimumDate = detailState.station?.startDate?.date?.toJavaLocalDate(),
                resolution = resolutions[selectedResolution],
                onDismiss = { historyContentViewModel.showGraphDatePicker(false) },
                onDateSelected = { date ->
                    historyContentViewModel.setSelectedGraphDate(date.toKotlinLocalDate())
                },
                dateToShow = LocalDate.now().minusYears(1)
            )
        }



        // Display the chart based on the selected resolution
        if (historyContentState.selectedElement != null && historyContentState.selectedLongTermDate != null) {
            when (resolutions[selectedResolution]) {
                "Denně" -> {
                    if (historyContentState.dailyAndMonthlyMeasurements != null) {
                        DailyChart(historyContentState.dailyAndMonthlyMeasurements!!.measurements)
                    } else {
                        CircularProgressIndicator()
                    }
                }
                "Měsíčně" -> {
                    if (historyContentState.monthlyMeasurements != null) {
                        MonthlyChart(historyContentState.monthlyMeasurements!!.measurements)
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
