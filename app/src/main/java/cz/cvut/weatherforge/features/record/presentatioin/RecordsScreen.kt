package cz.cvut.weatherforge.features.record.presentation

import ResolutionDatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.core.utils.getLocalizedDateString
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.record.presentatioin.RecordsScreenViewModel
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.data.model.StationElement
import kotlinx.datetime.toJavaLocalDate

import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreen(
    viewModel: RecordsScreenViewModel = koinViewModel(),
) {
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()

    LaunchedEffect(screenState.selectedStation, screenState.selectedDate) {
        if (screenState.selectedStation != null || screenState.selectedDate != null && screenState.selectedElement != null) {
            viewModel.fetchMeasurements()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.records_screen_title)) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (screenState.loading) {
                true -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.loading),
                        modifier = Modifier.align(Alignment.Center).padding(top = 60.dp)
                    )
                }
                false -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Element Dropdown
                        Text(
                            text = stringResource(R.string.select_element),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ElementDropdownMenu(
                            items = screenState.elementCodelist,
                            selectedItem = screenState.selectedElement,
                            onItemSelected = { element ->
                                viewModel.selectElement(element)
                            },
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Radio buttons to choose between Date and Station
                        Text(
                            text = "Filter by:",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        val options = listOf(
                            stringResource(R.string.option_date),
                            stringResource(R.string.option_station)
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            options.forEach { option ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = (option == screenState.selectedOption),
                                            onClick = { viewModel.setSelectedOption(option) }
                                        )
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (option == screenState.selectedOption),
                                        onClick = { viewModel.setSelectedOption(option) }
                                    )
                                    Text(
                                        text = option,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Conditional UI based on the selected option
                        when (screenState.selectedOption) {
                            stringResource(R.string.option_date) -> {
                                // Date selection button
                                Text(
                                    text = stringResource(R.string.date_picker_label),
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                OutlinedButton(
                                    onClick = { viewModel.showDatePicker(true) },
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
                                            text = screenState.selectedDate ?: stringResource(R.string.select_date),
                                            modifier = Modifier.padding(8.dp)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Dropdown",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                // Date picker dialog
                                if (screenState.showDatePicker) {
                                    ResolutionDatePickerDialog(
                                        minimumDate = LocalDate.now().minusYears(200),
                                        resolution = "Denně",
                                        onDismiss = { viewModel.showDatePicker(false) },
                                        onDateSelected = { date ->
                                            viewModel.setSelectedDate(date.toString())
                                            viewModel.showDatePicker(false)
                                        },
                                        dateToShow = LocalDate.now()
                                    )
                                }
                            }
                            stringResource(R.string.option_station) -> {
                                // Station search field
                                Text(
                                    text = stringResource(R.string.search_station_hint),
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                OutlinedTextField(
                                    value = screenState.searchQuery,
                                    onValueChange = { query ->
                                        viewModel.setSearchQuery(query)
                                        viewModel.filterStations(query)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text(stringResource(R.string.search_station)) },
                                    trailingIcon = {
                                        if (screenState.searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                                Icon(Icons.Default.Close, contentDescription = "Clear")
                                            }
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Display filtered stations
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp)
                                ) {
                                    items(
                                        items = screenState.filteredStations,
                                        key = { station -> station.stationId }
                                    ) { station ->
                                        StationItem(
                                            station = station,
                                            onClick = {
                                                viewModel.selectStation(station)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Display measurements in a table
                        if (screenState.measurements.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.measurements_table_label),
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            MeasurementsTable(measurements = screenState.measurements, selectedElement = screenState.selectedElement)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun StationItem(
    station: Station,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = station.location,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ElementDropdownMenu(
    items: List<ElementCodelistItem>,
    selectedItem: ElementCodelistItem?,
    onItemSelected: (ElementCodelistItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // Internal state for dropdown visibility

    val allowedElements = listOf("TMI", "TMA", "SCE", "SNO", "SRA", "FMAX")
    val filteredItems = items.filter { it.abbreviation in allowedElements }

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
                    text = selectedItem?.name ?: stringResource(R.string.select_element),
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
            filteredItems.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false // Close dropdown after selection
                    },
                    text = {
                        Text(text = item.name)
                    }
                )
            }
        }
    }
}
@Composable
fun MeasurementsTable(measurements: List<MeasurementDaily>, selectedElement: ElementCodelistItem?) {
    // Sort measurements by value
    var sortedMeasurements = measurements.sortedBy { it.value }
    if (selectedElement?.abbreviation == "TMI") {
        sortedMeasurements = sortedMeasurements.reversed()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Table header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.table_header_date),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.table_header_value),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }

        // Table rows
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(sortedMeasurements) { measurement ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = getLocalizedDateString(measurement.date.toJavaLocalDate()),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = measurement.value.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}