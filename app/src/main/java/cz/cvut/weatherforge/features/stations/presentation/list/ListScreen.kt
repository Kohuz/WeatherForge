package cz.cvut.weatherforge.features.stations.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.TextFieldDefaults
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
import cz.cvut.weatherforge.features.stations.data.Station
import org.koin.androidx.compose.koinViewModel


@Composable
fun ListScreen( viewModel: ListScreenViewModel = koinViewModel()) {
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
                            .padding(horizontal = 16.dp)
                    ) {
                        HorizontalDivider()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            FilterChangeButtons(
                                onFilterChange = viewModel::onFilterChange,
                                currentFilter = currentFilter
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        ResultHeading()
                        LazyColumn {
                            items(results) { station ->
                                ResultCard(
                                    station = station,
                                    onClick = {  }
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
    Button(onClick = { onFilterChange(ListScreenViewModel.Filter.Active)},
        colors = ButtonDefaults.buttonColors(
            containerColor = if (currentFilter == ListScreenViewModel.Filter.Active)
                Color.Red else MaterialTheme.colorScheme.secondary)
    )
    {
        Text(text = "Active")
    }
    Button(onClick = { onFilterChange(ListScreenViewModel.Filter.Inactive)},
        colors = ButtonDefaults.buttonColors(
            containerColor = if (currentFilter == ListScreenViewModel.Filter.Inactive)
                Color.Red else MaterialTheme.colorScheme.secondary)
    ){
        Text(text = "Inactive")
    }
    Button(onClick = { onFilterChange(ListScreenViewModel.Filter.All)},
        colors = ButtonDefaults.buttonColors(
            containerColor = if (currentFilter == ListScreenViewModel.Filter.All)
                Color.Red else MaterialTheme.colorScheme.secondary)
    ){
        Text(text = "All")
    }
}

@Composable
fun ResultCard(station: Station, onClick: () -> Unit) {
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




