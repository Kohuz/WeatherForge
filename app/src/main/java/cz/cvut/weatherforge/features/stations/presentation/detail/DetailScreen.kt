package cz.cvut.weatherforge.features.stations.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.LatLng
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.features.stations.data.model.isActive
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.GraphContent
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.GraphContentViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.DayContent
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.DayContentViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.HistoryContent
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.HistoryContentViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.OverviewContent
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    stationId: String,
    navigateUp: () -> Unit,
    navigateToDetail: (id: String) -> Unit,
    navigateToMap: (LatLng) -> Unit,
    detailScreenViewModel: DetailScreenViewModel = koinViewModel(),
    graphContentViewModel: GraphContentViewModel = koinViewModel(),
    dayContentViewModel: DayContentViewModel = koinViewModel(),
    historyContentViewModel: HistoryContentViewModel = koinViewModel()
) {
    val screenState by detailScreenViewModel.screenStateStream.collectAsStateWithLifecycle()
    val station = screenState.station
    val selectedTabIndex = screenState.selectedTabIndex
    val tabs = listOf("Přehled", "Časový průběh", "Dnešek v historii", "Roční srovnání")

    LaunchedEffect(stationId) {
        detailScreenViewModel.loadStation(stationId)
        detailScreenViewModel.loadRecords()
    }

    if (station != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = when (station.isActive()) {
                                            true -> Color.Green
                                            false -> Color.Gray
                                        },
                                        shape = CircleShape
                                    )
                                    .padding(end = 8.dp)
                            )
                            Column {
                                Text(station.location)

                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go back to list"
                            )
                        }
                    },
                    actions = {
                        // Add a button to navigate to the map
                        IconButton(onClick = {
                            val stationLatLng = LatLng(station.latitude, station.longitude)
                            navigateToMap(stationLatLng)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "View on Map"
                            )
                        }
                        IconButton(
                            onClick = {
                                detailScreenViewModel.toggleFavorite(station.stationId)
                            }
                        ) {
                            Icon(
                                imageVector = if (station.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                                tint = if (station.isFavorite) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                },
                                contentDescription = if (station.isFavorite) "Unfavorite" else "Favorite"
                            )
                        }
                        if(selectedTabIndex != 0) {
                            IconButton(onClick = { detailScreenViewModel.showHelpDialog() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = "Help",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
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
                if (!station.isActive()) {
                    WarningBanner()
                }
                // TabRow to display tabs
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            height = 3.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { detailScreenViewModel.selectTab(index) },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Display content based on the selected tab
                when (selectedTabIndex) {
                    0 -> OverviewContent(station, detailScreenViewModel, navigateToDetail)
                    1 -> GraphContent(station, detailScreenViewModel, graphContentViewModel)
                    2 -> DayContent(stationId, dayContentViewModel, detailScreenViewModel)
                    3 -> HistoryContent(stationId, historyContentViewModel, detailScreenViewModel)
                }
                if (screenState.showHelpDialog) {
                    HelpDialog(
                        selectedTabIndex = selectedTabIndex,
                        onDismiss = { detailScreenViewModel.dismissHelpDialog() }
                    )
                }
            }
        }
    }
}


@Composable
private fun HelpDialog(
    selectedTabIndex: Int,
    onDismiss: () -> Unit
) {
    val (title, message) = when (selectedTabIndex) {
        1 -> stringResource(R.string.help) to stringResource(R.string.help_dialog_message_graph).trimIndent()

        2 -> stringResource(R.string.help) to stringResource(R.string.help_day_content_message).trimIndent()

        3 -> stringResource(R.string.help) to stringResource(R.string.help_history_content_message).trimIndent()

        else -> "" to ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                Text(message)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.understood))
            }
        }
    )
}

@Composable
private fun WarningBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 12.dp, vertical = 8.dp), // Reduced vertical padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.SyncProblem,
            contentDescription = stringResource(R.string.station_inactive),
            tint = MaterialTheme.colorScheme.onErrorContainer,
        )
        Spacer(Modifier.width(8.dp)) // Reduced spacing
        Text(
            text = stringResource(R.string.station_inactive),
            style = MaterialTheme.typography.bodySmall.copy( // Smaller text
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

