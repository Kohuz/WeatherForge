package cz.cvut.weatherforge.features.record.presentatioin

import ResolutionDatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.core.utils.getLocalizedDateString
import cz.cvut.weatherforge.core.utils.getUnitByElementAbbreviation
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station
import kotlinx.datetime.toJavaLocalDate
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreen(
    viewModel: RecordsScreenViewModel = koinViewModel(),
) {
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()

    LaunchedEffect(screenState.selectedStation, screenState.selectedDate, screenState.selectedElement) {
        if (screenState.selectedStation != null || screenState.selectedDate != null && screenState.selectedElement != null) {
            viewModel.fetchMeasurements()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(R.string.records_screen_title),
                        style = MaterialTheme.typography.headlineLarge)
                }
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
                            .verticalScroll(rememberScrollState())
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

                        Text(
                            text = stringResource(R.string.date_picker_label),
                            style = MaterialTheme.typography.bodyLarge,
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


                        Spacer(modifier = Modifier.height(16.dp))

                        // Display measurements in a table
                        if (screenState.measurements.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f) // Takes all remaining space
                            ) {
                                MeasurementsTable(
                                    measurements = screenState.measurements,
                                    selectedElement = screenState.selectedElement,
                                    stations = screenState.allStations,
                                    elementCodelist = screenState.elementCodelist
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ElementDropdownMenu(
    items: List<ElementCodelistItem>,
    selectedItem: ElementCodelistItem?,
    onItemSelected: (ElementCodelistItem) -> Unit,
    allowedElements: List<String> = listOf("TMI", "TMA", "SCE", "SNO", "SRA", "Fmax")
) {
    var expanded by remember { mutableStateOf(false) } // Internal state for dropdown visibility

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
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            filteredItems.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false
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
fun MeasurementsTable(measurements: List<MeasurementDaily>, selectedElement: ElementCodelistItem?, stations: List<Station>, elementCodelist: List<ElementCodelistItem>) {

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
                text = stringResource(R.string.table_header_value),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f).padding(5.dp)
            )
            Text(
                text = stringResource(R.string.station),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }

        // Table rows
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(measurements) { measurement ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${measurement.value.toString()} ${getUnitByElementAbbreviation(measurement.element, elementCodelist)}",
                        modifier = Modifier.weight(1f).padding(5.dp)
                    )
                    stations.find { station -> station.stationId == measurement.stationId }?.let {
                        Text(
                            text = it.location,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}





