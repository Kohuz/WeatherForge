package cz.cvut.weatherforge.features.stations.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.core.utils.getLocalizedDateString
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.data.model.isActive
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import org.koin.androidx.compose.koinViewModel


@Composable
fun ListScreen(navigateToDetail: (id: String) -> Unit, viewModel: ListScreenViewModel = koinViewModel()) {
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()
    val results = screenState.results
    val loading = screenState.loading
    val currentSortingCriteria = screenState.sortingCriteria
    val currentFilter = screenState.currentFilter
    val dialogOpen = screenState.dialogOpen

    Scaffold(
        topBar = {
            TopSearchBar(
                screenState.currentQuery,
                viewModel::onQueryChange,
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (loading) {
                false -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))

                        // Filter and sorting controls section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(stringResource(R.string.filterBy))

                            FilterChangeButtons(
                                onFilterChange = viewModel::onFilterChange,
                                currentFilter = currentFilter,
                            )

                            Text(
                                stringResource(R.string.sortBy),
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            SortingOptions(
                                sortingCriteria = screenState.sortingCriteria,
                                ascendingOrder = screenState.ascendingOrder,
                                onSortingCriteriaChange = { criteria -> viewModel.setSortingCriteria(criteria) },
                                onAscendingOrderChange = { order -> viewModel.setAscendingOrder(order) }
                            )
                        }
                        LazyColumn {
                            items(results) { station ->
                                ResultCard(
                                    station = station,
                                    onClick = { navigateToDetail(station.stationId) },
                                    onToggleFavorite = { viewModel.toggleFavorite(station.stationId) },
                                    currentFilter = currentFilter,
                                    currentSortingCriteria = currentSortingCriteria
                                )
                            }
                        }
                    }
                }
                true -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }

    if (dialogOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.onDialogClose() },
            text = { Text(text = stringResource(R.string.failed_to_load_results)) },
            confirmButton = {
                Button(onClick = { viewModel.onDialogCloseRetry() }) {
                    Text(stringResource(R.string.retry))
                }
            }
        )
    }
}

/**
 * Displays filter chips for different station states (Active/Inactive/All/Favorites)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChangeButtons(
    onFilterChange: (StationFilter) -> Unit,
    currentFilter: StationFilter,
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        FilterChip(
            selected = currentFilter == StationFilter.Active,
            onClick = { onFilterChange(StationFilter.Active) },
            label = { Text(stringResource(R.string.active)) },

        )

        FilterChip(
            selected = currentFilter == StationFilter.Inactive,
            onClick = { onFilterChange(StationFilter.Inactive) },
            label = { Text(stringResource(R.string.inactive)) },

        )

        FilterChip(
            selected = currentFilter == StationFilter.All,
            onClick = { onFilterChange(StationFilter.All) },
            label = { Text(stringResource(R.string.all)) },

        )

        FilterChip(
            selected = currentFilter == StationFilter.Favorites,
            onClick = { onFilterChange(StationFilter.Favorites) },
            label = { Text(stringResource(R.string.favorites)) },
        )
    }
}

/**
 * Displays a single station item with its details and favorite toggle
 */
@Composable
fun ResultCard(
    station: Station,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    currentFilter: StationFilter,
    currentSortingCriteria: SortingOption
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        // Station name
        Text(
            modifier = Modifier.padding(8.dp),
            text = station.location,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (station.isActive()) {
            Icon(
                imageVector = Icons.Filled.Circle,
                contentDescription = "Active Station",
                tint = Color.Green,
            )
            Spacer(Modifier.width(4.dp))
        }
        if (currentFilter == StationFilter.Inactive) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = getLocalizedDateString(station.endDate.date.toJavaLocalDate()),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            imageVector = if (station.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
            contentDescription = if (station.isFavorite) "Remove from favorites" else "Add to favorites",
            tint = if (station.isFavorite) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            },
            modifier = Modifier
                .padding(start = 4.dp)
                .clickable { onToggleFavorite() }
        )
        if(currentSortingCriteria === SortingOption.Elevation && station.elevation.toInt() != -1) {
            Text(text = "${station.elevation.toInt()} m n.m.")
        }
        if(currentSortingCriteria === SortingOption.BeginDate && station.startDate != null) {
            Text(text = getLocalizedDateString(station.startDate.date.toJavaLocalDate()))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(query: String, onQueryChange: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = { newQuery -> onQueryChange(newQuery) },
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "searchIcon",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.enter_your_search),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "clear text",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .clickable {
                                onQueryChange("")
                            }
                    )
                },

            )
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SortingOptions(
    sortingCriteria: SortingOption,
    ascendingOrder: Boolean,
    onSortingCriteriaChange: (SortingOption) -> Unit,
    onAscendingOrderChange: (Boolean) -> Unit,
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        FilterChip(
            selected = sortingCriteria == SortingOption.Elevation,
            onClick = {
                if (sortingCriteria == SortingOption.Elevation) {
                    onAscendingOrderChange(!ascendingOrder)
                } else {
                    onSortingCriteriaChange(SortingOption.Elevation)
                    onAscendingOrderChange(true)
                }
            },
            label = {
                Text(
                    stringResource(R.string.elevation),
                    maxLines = 2,
                    softWrap = true,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(80.dp)
                )
            },
            trailingIcon = {
                if (sortingCriteria == SortingOption.Elevation) {
                    Icon(
                        imageVector = if (ascendingOrder) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.sorting_order)
                    )
                }
            }
        )

        FilterChip(
            selected = sortingCriteria == SortingOption.BeginDate,
            onClick = {
                if (sortingCriteria == SortingOption.BeginDate) {
                    onAscendingOrderChange(!ascendingOrder)
                } else {
                    onSortingCriteriaChange(SortingOption.BeginDate)
                    onAscendingOrderChange(true)
                }
            },
            label = {  Text(
                stringResource(R.string.begin_date),
                maxLines = 2,
                softWrap = true,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(80.dp)
            ) },
            trailingIcon = {
                if (sortingCriteria == SortingOption.BeginDate) {
                    Icon(
                        imageVector = if (ascendingOrder) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.sorting_order)
                    )
                }
            }
        )

        FilterChip(
            selected = sortingCriteria == SortingOption.Alphabetical,
            onClick = {
                if (sortingCriteria == SortingOption.Alphabetical) {
                    onAscendingOrderChange(!ascendingOrder)
                } else {
                    onSortingCriteriaChange(SortingOption.Alphabetical)
                    onAscendingOrderChange(true)
                }
            },
            label = {  Text(
                stringResource(R.string.alphabetical),
                maxLines = 2,
                softWrap = true,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(80.dp)
            ) },
            trailingIcon = {
                if (sortingCriteria == SortingOption.Alphabetical) {
                    Icon(
                        imageVector = if (ascendingOrder) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.sorting_order)
                    )
                }
            }
        )
    }
}

