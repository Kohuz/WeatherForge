package cz.cvut.weatherforge.features.record.presentatioin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import org.koin.androidx.compose.koinViewModel


@Composable
fun RecordsScreen(
    viewModel: RecordsScreenViewModel = koinViewModel(),
) {
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()

    when (screenState.loading) {
        false -> {
            Scaffold { paddingValues ->
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text("Records screen")

                        // Element selection
                        Text("Select Element:")
                        ElementDropdownMenu(
                            items = screenState.elementCodelist,
                            onItemSelected = { element ->
                                viewModel.selectElement(element)
                            }
                        )

                        // Station selection (you can replace this with a proper station selection UI)
                        Text("Select Station:")
                        Button(onClick = {
                            // Implement station selection logic
                        }) {
                            Text("Select Station")
                        }

                        // Date selection (you can replace this with a proper date picker UI)
                        Text("Select Date:")
                        Button(onClick = {
                            // Implement date selection logic
                        }) {
                            Text("Select Date")
                        }

                        // Fetch measurements button
                        Button(onClick = {
                            viewModel.fetchMeasurements()
                        }) {
                            Text("Fetch Measurements")
                        }

                        // Display measurements
                        screenState.measurements.forEach { measurement ->
                            Text("Measurement: ${measurement.value}")
                        }
                    }
                }
            }
        }
        true -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ElementDropdownMenu(
    items: List<ElementCodelistItem>,
    onItemSelected: (ElementCodelistItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<ElementCodelistItem?>(null) }

    Box {
        // Button to toggle the dropdown menu
        Button(onClick = { expanded = true }) {
            Text(selectedItem?.name ?: "Select Element")
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Iterate through the items and create a DropdownMenuItem for each
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name) }, // Use the `text` parameter
                    onClick = {
                        selectedItem = item
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

