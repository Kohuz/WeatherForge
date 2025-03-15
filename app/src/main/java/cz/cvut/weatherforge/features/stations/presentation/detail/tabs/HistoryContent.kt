package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import ResolutionDatePickerDialog
import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
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
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.DailyDatePicker
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.DailyChart
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.MonthlyChart
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.YearlyChart
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate



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



    LaunchedEffect(historyContentState.selectedDate) {
        historyContentViewModel.fetchConcreteDayData(stationId)
    }

    LaunchedEffect(historyContentState.selectedLongTermDate) {
        historyContentViewModel.fetchLongTermStats(stationId)
    }



        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Date Picker for Day and Month
            OutlinedButton(
                onClick = { historyContentViewModel.showLongTermDatePicker(true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        R.string.select_day_month,
                        historyContentState.selectedLongTermDate?.toString() ?: stringResource(R.string.no_date_selected)
                    )
                )
            }

            if (historyContentState.showLongTermDatePicker) {
                ResolutionDatePickerDialog (
                    minimumDate = historyContentState.selectedLongTermDate?.toJavaLocalDate(),
                    resolution = resolutions[selectedResolution],
                    onDismiss = { historyContentViewModel.showLongTermDatePicker(false) },
                    onDateSelected = { date -> historyContentViewModel.setSelectedLongTermDate(date.toKotlinLocalDate())}
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
            }
            else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = historyContentState.selectedElement?.name ?: stringResource(R.string.detail_select_station_element),
                    modifier = Modifier.padding(8.dp)
                )
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
                            // Set the fromDate to the beginDate of the selected element
                            val beginDate = detailState.station?.stationElements
                                ?.find { it.elementAbbreviation == element.abbreviation }
                                ?.beginDate

                            historyContentViewModel.toggleDropdown(false)
                        },
                        text = {
                            Text(text = element.name)
                        }
                    )
                }
            }

            if (historyContentState.selectedElement != null && historyContentState.selectedLongTermDate != null) {
                // Fetch measurements based on the selected resolution
                LaunchedEffect(historyContentState.selectedElement, historyContentState.selectedLongTermDate, selectedResolution) {
                    when (resolutions[selectedResolution]) {
                        "Denně" -> {
                            historyContentViewModel.fetchDailyMeasurements(
                                stationId = stationId,
                                element = historyContentState.selectedElement!!.abbreviation,
                                date = historyContentState.selectedLongTermDate!!
                            )
                        }
                        "Měsíčně" -> {
                            historyContentViewModel.fetchMonthlyMeasurements(
                                stationId = stationId,
                                element = historyContentState.selectedElement!!.abbreviation,
                                date = historyContentState.selectedLongTermDate!!
                            )
                        }
                    }
                }

                // Display the chart based on the selected resolution
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

            // Date Picker for Full Date
            OutlinedButton(
                onClick = { historyContentViewModel.showDatePicker(true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        R.string.select_date,
                        historyContentState.selectedDate?.toString() ?: stringResource(R.string.no_date_selected)
                    )
                )
            }



            if (historyContentState.showDatePicker) {
                DailyDatePicker(
                    minimumDate = detailState.station?.startDate?.date?.toJavaLocalDate(),
                    onDismiss = { historyContentViewModel.showDatePicker(false) },
                    onDateSelected = { date ->
                        historyContentViewModel.setSelectedDate(date.toKotlinLocalDate())
                    }
                )

            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

