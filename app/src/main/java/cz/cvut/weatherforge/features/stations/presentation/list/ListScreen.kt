package com.kozubek.livesport.features.sportEntries.presentation.list

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.features.stations.presentation.list.ListScreenViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navigateToDetail: (id: String) -> Unit,
               viewModel: ListScreenViewModel = koinViewModel()) {
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()
    val results = screenState.results
    val loading = screenState.loading
    val currentFilter = screenState.currentFilter
    val dialogOpen = screenState.dialogOpen


    Scaffold(
        topBar = { TopSearchBar(screenState.currentQuery,
            viewModel::onQueryChange, viewModel::onEntriesSearched) }
    ){
        when(loading) {
            false -> {  Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        HorizontalDivider()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(6.dp)
                        ) {

                            FilterChangeButtons(
                                onFilterChange = viewModel::onFilterChange,
                                currentFilter = currentFilter
                            )

                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        ResultHeading()
                LazyColumn{

                        results {resiůt ->
                            ResultCard(entry,
                                onClick = { navigateToDetail(entry.id)})
                            Divider()
                        }
                    }
                }
                    }
                //Request failed
                if(dialogOpen){AlertDialog(
                    onDismissRequest = {viewModel.onDialogClose()},
                    text = {Text(text = "Failed to load results")},
                    confirmButton = {
                        Button(
                            onClick = {viewModel.onDialogCloseRetry()}
                        ) {
                            Text("Retry")
                        }
                    })
                }
            }
            //Loading true
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
    Button(onClick = { onFilterChange(ListScreenViewModel.Filter.All)},
        colors = ButtonDefaults.buttonColors(
            containerColor = if (currentFilter == ListScreenViewModel.Filter.All)
                Color.Red else MaterialTheme.colorScheme.secondary)
    )
    {
        Text(text = "All")
    }
    Button(onClick = { onFilterChange(ListScreenViewModel.Filter.Participants)},
        colors = ButtonDefaults.buttonColors(
            containerColor = if (currentFilter == ListScreenViewModel.Filter.Participants)
                Color.Red else MaterialTheme.colorScheme.secondary)
    ){
        Text(text = "Participants")
    }
    Button(onClick = { onFilterChange(ListScreenViewModel.Filter.Competitions)},
        colors = ButtonDefaults.buttonColors(
            containerColor = if (currentFilter == ListScreenViewModel.Filter.Competitions)
                Color.Red else MaterialTheme.colorScheme.secondary)
    ){
        Text(text = "Competitions")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategorizedLazyColumn(categories: List<SportCategory>, navigateToDetail: (id: String) -> Unit) {
    LazyColumn{
        categories.forEach{ category ->
            stickyHeader{
                SportHeader(text = category.name)
            }
            items(category.items) {entry ->
                ResultCard(entry,
                    onClick = { navigateToDetail(entry.id)})
                Divider()
            }
        }
    }
}

@Composable
fun ResultCard(entry: SportEntry, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }) {
        //TODO: extract this to a separate component
        AsyncImage(
            model = "https://www.livesport.cz/res/image/data/${entry.images.getOrNull(0)?.path ?: ""}",
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .size(50.dp),
            contentDescription = "entry photo",
            error = rememberVectorPainter(image = Icons.Default.AccountCircle)
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = entry.name,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TopSearchBar(query: String, onQueryChange: (String) -> Unit, onEntriesSearched: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TopAppBar(
        modifier = Modifier.padding(vertical = 5.dp),
        title = {
            TextField(value = query,
            onValueChange = { newQuery -> onQueryChange(newQuery) },
            keyboardActions = KeyboardActions(
                onDone = {
                    onEntriesSearched()
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
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
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
fun SportHeader(text: String){
    Text(text = text.uppercase(),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary))


}

data class SportCategory(
    val name: String,
    val items: List<SportEntry>
)



