package cz.cvut.weatherforge.features.record.presentatioin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text("Records screen")

                        // Element selection
                        Text("Select Element:")
                        DropdownMenu(
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


