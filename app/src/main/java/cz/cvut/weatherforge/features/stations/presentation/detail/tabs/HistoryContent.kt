package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import androidx.compose.runtime.Composable
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreenViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.weatherforge.R
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryContent(
    stationId: String,
    viewModel: HistoryContentViewModel
) {
    // Observe the state from the ViewModel
    val state by viewModel.historyContentState.collectAsStateWithLifecycle()

    // Fetch data automatically when the selectedDate changes
    LaunchedEffect(state.selectedDate) {
        if (state.selectedDate != null) {
            viewModel.setSelectedDate(state.selectedDate!!)
            viewModel.fetchAllData(stationId)
        }
        else {
            viewModel.setSelectedDate(LocalDate.now().toKotlinLocalDate())
            viewModel.fetchAllData(stationId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Date Picker Button
        OutlinedButton(
            onClick = { viewModel.showDatePicker(true) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Date: ${state.selectedDate ?: "No date selected"}")
        }

        // Show the date picker dialog if needed
        if (state.showDatePicker) {
            DatePickerDialog(
                onDismiss = { viewModel.showDatePicker(false) },
                onDateSelected = { date -> viewModel.setSelectedDate(date) }
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

        if (state.dailyStats != null) {
            Column (
            Modifier.fillMaxWidth()
        ) {
            Row (Modifier.fillMaxWidth()) {
                Column {
                    Text(stringResource(R.string.temperature))
                    Text("${stringResource(R.string.min)}: ${state.dailyStats!!.valueStats.find {it.element == "TMI"}?.lowest}")
                    Text("${stringResource(R.string.max)}: ${state.dailyStats!!.valueStats.find {it.element == "TMA"}?.highest}")
                    Text("${stringResource(R.string.average)}: ${state.dailyStats!!.valueStats.find {it.element == "T"}?.average?.let { String.format("%.1f", it) }}")
                }
                Column {
                    Text(stringResource(R.string.precipitation))
                    Text("${stringResource(R.string.max)}: ${state.dailyStats!!.valueStats.find {it.element == "TMA"}?.highest}")
                    Text("${stringResource(R.string.average)}: ${state.dailyStats!!.valueStats.find {it.element == "T"}?.average?.let { String.format("%.1f", it) }}")
                }

            }
            Row (Modifier.fillMaxWidth()) {
                Column {
                    Text(stringResource(R.string.wind))
                    Text("${stringResource(R.string.max)}: ${state.dailyStats!!.valueStats.find {it.element == "FMAX"}?.highest}")
                    Text("${stringResource(R.string.average)}: ${state.dailyStats!!.valueStats.find {it.element == "F"}?.average?.let { String.format("%.1f", it) }}")
                }
                Column {
                    //TODO
                    Text(stringResource(R.string.snow))
                    Text("${stringResource(R.string.max)}: ${state.dailyStats!!.valueStats.find {it.element == "TMA"}?.highest}")
                    Text("${stringResource(R.string.average)}: ${state.dailyStats!!.valueStats.find {it.element == "T"}?.average?.let { String.format("%.1f", it) }}")
                }

            }
        }
        }

//
//        if (state.dailyAndMonthlyMeasurements != null) {
//            Text("Daily and Monthly Measurements: ${state.dailyAndMonthlyMeasurements!!.measurements}")
//        }
//
//        if (state.monthlyMeasurements != null) {
//            Text("Monthly Measurements: ${state.monthlyMeasurements!!.measurements}")
//        }
//
//        if (state.statsDay != null) {
//            Text("Daily Stats: ${state.statsDay!!.measurements}")
//        }
    }
}