package cz.cvut.weatherforge.features.stations.presentation.detail

import WeatherCard
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    LaunchedEffect(stationId) {
        viewModel.loadStation(stationId)
    }
    if (station != null) {//TODO: FIX THIS
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text(text = station.location)
                },
                    navigationIcon = {
                        IconButton(onClick = { navigateUp() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Go back to list"
                            )
                        }
                    })
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = station.location,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )

            }
        }
    }
}