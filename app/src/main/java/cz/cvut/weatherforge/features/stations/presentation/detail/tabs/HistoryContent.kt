package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.features.stations.presentation.detail.DayMonthPickerDialog
import kotlinx.datetime.toKotlinLocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryContent(
    stationId: String,
    viewModel: HistoryContentViewModel
) {
    val state by viewModel.historyContentState.collectAsStateWithLifecycle()

    LaunchedEffect(state.selectedDate, state.selectedDayMonthDate) {
        viewModel.fetchConcreteDayData(stationId)
    }

    LaunchedEffect(state.selectedDayMonthDate) {
        viewModel.fetchLongTermStats(stationId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())

    ) {


        // Second Date Picker (Day and Month Only)
        OutlinedButton(
            onClick = { viewModel.showDayMonthDatePicker(true) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Day and Month: ${state.selectedDayMonthDate?.toString() ?: "No date selected"}")
        }

        // Show the custom day and month picker dialog if needed
        if (state.showDayMonthPicker) {
            DayMonthPickerDialog(
                onDismiss = { viewModel.showDayMonthDatePicker(false) },
                onDateSelected = { date -> viewModel.setSelectedDayMonthDate(date.toKotlinLocalDate()) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display loading state
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // Display error message
        if (state.error != null) {
            Text(
                text = "Error: ${state.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Display data
        if (state.dailyStats != null) {
            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth()) {
                    Column {
                        Text(stringResource(R.string.temperature))
                        Text("${stringResource(R.string.min)}: ${state.dailyStats!!.valueStats.find { it.element == "TMI" }?.lowest}")
                        Text("${stringResource(R.string.max)}: ${state.dailyStats!!.valueStats.find { it.element == "TMA" }?.highest}")
                        Text("${stringResource(R.string.average)}: ${state.dailyStats!!.valueStats.find { it.element == "T" }?.average?.let { String.format("%.1f", it) }}")
                    }
                    Column {
                        Text(stringResource(R.string.precipitation))
                        Text("${stringResource(R.string.max)}: ${state.dailyStats!!.valueStats.find { it.element == "TMA" }?.highest}")
                        Text("${stringResource(R.string.average)}: ${state.dailyStats!!.valueStats.find { it.element == "T" }?.average?.let { String.format("%.1f", it) }}")
                    }
                }
                Row(Modifier.fillMaxWidth()) {
                    Column {
                        Text(stringResource(R.string.wind))
                        Text("${stringResource(R.string.max)}: ${state.dailyStats!!.valueStats.find { it.element == "FMAX" }?.highest}")
                        Text("${stringResource(R.string.average)}: ${state.dailyStats!!.valueStats.find { it.element == "F" }?.average?.let { String.format("%.1f", it) }}")
                    }
                    Column {
                        Text(stringResource(R.string.snow))
                        Text("${stringResource(R.string.max)}: ${state.dailyStats!!.valueStats.find { it.element == "TMA" }?.highest}")
                        Text("${stringResource(R.string.average)}: ${state.dailyStats!!.valueStats.find { it.element == "T" }?.average?.let { String.format("%.1f", it) }}")
                    }
                }
            }
        }
        OutlinedButton(
            onClick = { viewModel.showDatePicker(true) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Date 1: ${state.selectedDate ?: "No date selected"}")
        }

        if (state.showDatePicker) {
            DatePickerDialog(
                onDismiss = { viewModel.showDatePicker(false) },
                onDateSelected = { date -> viewModel.setSelectedDate(date) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}