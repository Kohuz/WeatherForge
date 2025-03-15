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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                            HorizontalDivider()
                            Column (
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
                            ResultHeading()
                            LazyColumn {
                                items(results) { station ->
                                    ResultCard(
                                        station = station,
                                        onClick = {navigateToDetail(station.stationId)},
                                        onToggleFavorite = { viewModel.toggleFavorite(station.stationId) }
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
                            CircularProgressIndicator()
                    }
                }
            }
        }
    }

    if (dialogOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.onDialogClose() },
            text = { Text(text = "Failed to load results") },
            confirmButton = {
                Button(onClick = { viewModel.onDialogCloseRetry() }) {
                    Text("Retry")
                }
            }
        )
    }
}

@Composable
fun ResultHeading() {
    Text(modifier = Modifier
        .fillMaxWidth()
        .background(color = MaterialTheme.colorScheme.secondary)
        .padding(5.dp),
        text = "Results",
        color = Color.White,
        fontSize = 20.sp)
    }

@Composable
fun FilterChangeButtons(onFilterChange: (ListScreenViewModel.Filter) -> Unit,
                        currentFilter: ListScreenViewModel.Filter) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
    Button(onClick = { onFilterChange(ListScreenViewModel.Filter.Active)},

    )
    {
        Text(text = "Active")
    }
    Button(onClick = { onFilterChange(ListScreenViewModel.Filter.Inactive)},

    ){
        Text(text = "Inactive")
    }
    Button(onClick = { onFilterChange(ListScreenViewModel.Filter.All)},

    ){
        Text(text = "All")
    }
    Button(onClick = { onFilterChange(ListScreenViewModel.Filter.Favorites) }) {
        Text(text = "Favorites")
    }
    }
}

@Composable
fun ResultCard(station: Station, onClick: () -> Unit, onToggleFavorite: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = station.location,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        if (station.isActive()) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Active Station",
                tint = Color.Green
            )
        }
        Icon(
            imageVector = if (station.isFavorite) Icons.Outlined.Star else Icons.Outlined.Star,
            contentDescription = "Favorite Station",
            tint = if (station.isFavorite) Color.Yellow else Color.Gray,
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
            TextField(value = query,
            onValueChange = { newQuery -> onQueryChange(newQuery) },
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
            ,
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "searchIcon")
            },
            placeholder = { Text(text = "Enter your search") },

            trailingIcon = {
                Icon(Icons.Default.Clear,
                    contentDescription = "clear text",
                    modifier = Modifier
                        .clickable {
                            onQueryChange("")
                        }
                )
            }
        )},
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
        // Button for sorting by Elevation
        Button(
            onClick = {
                if (sortingCriteria == "Elevation") {
                    // Toggle order if the same button is clicked
                    onAscendingOrderChange(!ascendingOrder)
                } else {
                    // Set new sorting criteria and default to ascending order
                    onSortingCriteriaChange("Elevation")
                    onAscendingOrderChange(true)
                }
            },

        ) {
            Text(text = "Elevation")
            if (sortingCriteria == "Elevation") {
                Icon(
                    imageVector = if (ascendingOrder) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (ascendingOrder) "Ascending" else "Descending",
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Button for sorting by Begin Date
        Button(
            onClick = {
                if (sortingCriteria == "Begin Date") {
                    // Toggle order if the same button is clicked
                    onAscendingOrderChange(!ascendingOrder)
                } else {
                    // Set new sorting criteria and default to ascending order
                    onSortingCriteriaChange("Begin Date")
                    onAscendingOrderChange(true)
                }
            },

        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Begin date")
                if (sortingCriteria == "Begin Date") {
                    Icon(
                        imageVector = if (ascendingOrder) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (ascendingOrder) "Ascending" else "Descending",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        // Button for sorting Alphabetically
        Button(
            onClick = {
                if (sortingCriteria == "Alphabetical") {
                    // Toggle order if the same button is clicked
                    onAscendingOrderChange(!ascendingOrder)
                } else {
                    // Set new sorting criteria and default to ascending order
                    onSortingCriteriaChange("Alphabetical")
                    onAscendingOrderChange(true)
                }
            },

        ) {
            Text(text = "Alphabetical")
            if (sortingCriteria == "Alphabetical") {
                Icon(
                    imageVector = if (ascendingOrder) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (ascendingOrder) "Ascending" else "Descending",
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}


