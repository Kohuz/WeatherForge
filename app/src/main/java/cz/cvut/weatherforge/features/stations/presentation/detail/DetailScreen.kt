package cz.cvut.weatherforge.features.stations.presentation.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.GraphContent
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.HistoryContent
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.OverviewContent
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.TableContent
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    stationId: String,
    navigateUp: () -> Unit,
    viewModel: DetailScreenViewModel = koinViewModel()
) {
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()
    val station = screenState.station
    val selectedTabIndex = screenState.selectedTabIndex
    val tabs = listOf("Přehled", "Graf", "Tabulka", "Dnešek v historii") //TODO: Localize

    LaunchedEffect(stationId) {
        viewModel.loadStation(stationId)
    }

    if (station != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = station.location) },
                    navigationIcon = {
                        IconButton(onClick = { navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go back to list"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TabRow to display tabs
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = selectedTabIndex == index,
                            onClick = { viewModel.selectTab(index) }
                        )
                    }
                }

                // Display content based on the selected tab
                when (selectedTabIndex) {
                    0 -> OverviewContent(station, viewModel)
                    1 -> GraphContent(station, viewModel)
                    2 -> TableContent(station, viewModel)
                    3 -> HistoryContent(station, viewModel)
                }
            }
        }
    }
}





