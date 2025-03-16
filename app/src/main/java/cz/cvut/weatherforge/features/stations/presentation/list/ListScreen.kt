package cz.cvut.weatherforge.features.stations.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.data.model.isActive
import org.koin.androidx.compose.koinViewModel


@Composable
fun ListScreen(navigateToDetail: (id: String) -> Unit, viewModel: ListScreenViewModel = koinViewModel()) {
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()
    val results = screenState.results
    val loading = screenState.loading
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
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)) // Use onSurface for divider
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            FilterChangeButtons(
                                onFilterChange = viewModel::onFilterChange,
                                currentFilter = currentFilter
                            )
                            SortingOptions(
                                sortingCriteria = screenState.sortingCriteria,
                                ascendingOrder = screenState.ascendingOrder,
                                onSortingCriteriaChange = { criteria -> viewModel.setSortingCriteria(criteria) },
                                onAscendingOrderChange = { order -> viewModel.setAscendingOrder(order) }
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        LazyColumn {
                            items(results) { station ->
                                ResultCard(
                                    station = station,
                                    onClick = { navigateToDetail(station.stationId) },
                                    onToggleFavorite = { viewModel.toggleFavorite(station.stationId) },
                                    sortingCriteria = screenState.sortingCriteria,
                                    currentFilter = currentFilter
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
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) // Use primary color for progress indicator
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

@Composable
fun FilterChangeButtons(
    onFilterChange: (ListScreenViewModel.Filter) -> Unit,
    currentFilter: ListScreenViewModel.Filter
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onFilterChange(ListScreenViewModel.Filter.Active) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // Use primary color for buttons
        ) {
            Text(text = stringResource(R.string.active))
        }
        Button(
            onClick = { onFilterChange(ListScreenViewModel.Filter.Inactive) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = stringResource(R.string.inactive))
        }
        Button(
            onClick = { onFilterChange(ListScreenViewModel.Filter.All) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = stringResource(R.string.all))
        }
        Button(
            onClick = { onFilterChange(ListScreenViewModel.Filter.Favorites) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = stringResource(R.string.favorites))
        }
    }
}

@Composable
fun ResultCard(
    station: Station,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    sortingCriteria: String,
    currentFilter: ListScreenViewModel.Filter
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surface) // Use surface color for cards
            .padding(8.dp)
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = station.location,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface // Use onSurface color for text
        )

        if (station.isActive()) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Active Station",
                tint = MaterialTheme.colorScheme.primary // Use primary color for active icon
            )
        }
        if (currentFilter == ListScreenViewModel.Filter.Inactive) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = station.endDate.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface // Use onSurface color for text
            )
        }
        Icon(
            imageVector = if (station.isFavorite) Icons.Outlined.Star else Icons.Outlined.Star,
            contentDescription = "Favorite Station",
            tint = if (station.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // Use primary and onSurface colors for favorite icon
            modifier = Modifier
                .padding(start = 4.dp)
                .clickable { onToggleFavorite() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(query: String, onQueryChange: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TopAppBar(
        modifier = Modifier.padding(vertical = 5.dp),
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
                    .fillMaxWidth()
                    .padding(10.dp),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "searchIcon",
                        tint = MaterialTheme.colorScheme.onSurface // Use onSurface color for icon
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.enter_your_search),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Use onSurface color for placeholder
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "clear text",
                        tint = MaterialTheme.colorScheme.onSurface, // Use onSurface color for icon
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

@Composable
fun SortingOptions(
    sortingCriteria: String,
    ascendingOrder: Boolean,
    onSortingCriteriaChange: (String) -> Unit,
    onAscendingOrderChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                if (sortingCriteria == "Elevation") {
                    onAscendingOrderChange(!ascendingOrder)
                } else {
                    onSortingCriteriaChange("Elevation")
                    onAscendingOrderChange(true)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // Use primary color for buttons
        ) {
            Text(text = stringResource(R.string.elevation))
            if (sortingCriteria == "Elevation") {
                Icon(
                    imageVector = if (ascendingOrder) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (ascendingOrder) "Ascending" else "Descending",
                    tint = MaterialTheme.colorScheme.onPrimary // Use onPrimary color for icon
                )
            }
        }

        Button(
            onClick = {
                if (sortingCriteria == "Begin Date") {
                    onAscendingOrderChange(!ascendingOrder)
                } else {
                    onSortingCriteriaChange("Begin Date")
                    onAscendingOrderChange(true)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = stringResource(R.string.begin_date))
            if (sortingCriteria == "Begin Date") {
                Icon(
                    imageVector = if (ascendingOrder) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (ascendingOrder) "Ascending" else "Descending",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Button(
            onClick = {
                if (sortingCriteria == "Alphabetical") {
                    onAscendingOrderChange(!ascendingOrder)
                } else {
                    onSortingCriteriaChange("Alphabetical")
                    onAscendingOrderChange(true)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = stringResource(R.string.alphabetical))
            if (sortingCriteria == "Alphabetical") {
                Icon(
                    imageVector = if (ascendingOrder) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (ascendingOrder) "Ascending" else "Descending",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}