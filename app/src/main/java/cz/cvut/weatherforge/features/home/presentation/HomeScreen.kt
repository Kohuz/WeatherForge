package cz.cvut.weatherforge.features.home.presentation

import SwipeableWeatherCard
import WeatherCard
import WeatherCardData
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.ui.theme.AppTypography
import org.koin.androidx.compose.koinViewModel
import kotlin.math.pow
import kotlin.math.sqrt


@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = koinViewModel()) {
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.fetchUserLocation(context, fusedLocationClient)
        } else {
            Log.e("locationScreen", "Location permission was denied by the user.")
        }
    }

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                viewModel.fetchUserLocation(context, fusedLocationClient)
            }
            else -> {
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    when (screenState.loading) {
        false -> {
            Scaffold(
                topBar = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = stringResource(R.string.nearest_station),
                            style = AppTypography.headlineLarge,
                        )
                        screenState.closestStation?.let {
                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = screenState.closestStation!!.location,
                                style = AppTypography.headlineMedium,
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                    Text("Inside box")
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {Log.d("HomeScreen", "Column is rendering") // Debugging
                        Text("Inside Column")

                        SwipeableWeatherCard(weatherData = screenState.weatherData)
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


fun mapStationToWeatherCardData(
    station: Station,
    userLat: Double,
    userLon: Double
): WeatherCardData {
    val distanceDegrees = pythagoreanDistance(userLat, userLon, station.latitude, station.longitude)
    val distanceKm = distanceDegrees * 111.32

    return WeatherCardData(
        title = station.location,
        items = listOf(
            "Distance" to "%.2f km".format(distanceKm)
        )
    )
}



fun pythagoreanDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dx = lat2 - lat1
    val dy = lon2 - lon1
    return sqrt(dx. pow(2) + dy.pow(2))
}


