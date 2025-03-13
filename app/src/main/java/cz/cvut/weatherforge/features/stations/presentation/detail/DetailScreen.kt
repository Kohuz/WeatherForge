package cz.cvut.weatherforge.features.stations.presentation.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.GraphContent
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.GraphContentViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.HistoryContent
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.HistoryContentViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.OverviewContent
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.TableContent
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    stationId: String,
    navigateUp: () -> Unit,
    detailScreenViewModel: DetailScreenViewModel = koinViewModel(),
    graphContentViewModel: GraphContentViewModel = koinViewModel(),
    historyContentViewModel: HistoryContentViewModel = koinViewModel()

) {
    val screenState by detailScreenViewModel.screenStateStream.collectAsStateWithLifecycle()
    val station = screenState.station
    val selectedTabIndex = screenState.selectedTabIndex
    val tabs = listOf("Přehled", "Graf", "Tabulka", "Dnešek v historii") //TODO: Localize

    LaunchedEffect(stationId) {
        detailScreenViewModel.loadStation(stationId)
        detailScreenViewModel.loadRecords()
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
                            onClick = { detailScreenViewModel.selectTab(index) }
                        )
                    }
                }

                // Display content based on the selected tab
                when (selectedTabIndex) {
                    0 -> OverviewContent(station, detailScreenViewModel)
                    1 -> GraphContent(station, detailScreenViewModel,graphContentViewModel)
                    2 -> TableContent(station, detailScreenViewModel)
                    3 -> HistoryContent(stationId, historyContentViewModel, detailScreenViewModel)
                }
            }
        }
    }
}





