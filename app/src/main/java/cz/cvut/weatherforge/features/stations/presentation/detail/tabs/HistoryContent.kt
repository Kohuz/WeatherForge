package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import androidx.compose.runtime.Composable
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreenViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

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

        // Display the results
        if (state.dailyStats != null) {
            Text("Daily Stats (Long Term): ${state.dailyStats!!.valueStats}")
        }

        if (state.dailyAndMonthlyMeasurements != null) {
            Text("Daily and Monthly Measurements: ${state.dailyAndMonthlyMeasurements!!.measurements}")
        }

        if (state.monthlyMeasurements != null) {
            Text("Monthly Measurements: ${state.monthlyMeasurements!!.measurements}")
        }

        if (state.statsDay != null) {
            Text("Daily Stats: ${state.statsDay!!.measurements}")
        }
    }
}