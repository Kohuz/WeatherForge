package cz.cvut.weatherforge.features.home.presentation

import InfoCard
import InfoCardData
import NearbyStationInfoCard
import SwipeableInfoCard
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementLatest
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.presentation.detail.elementAbbreviationToNameUnitPair
import cz.cvut.weatherforge.ui.theme.AppTypography
import org.koin.androidx.compose.koinViewModel
import kotlin.math.pow
import kotlin.math.sqrt


@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = koinViewModel(),
    navigateToDetail: (id: String) -> Unit
) {
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
                // Permission already granted, fetch location
                viewModel.fetchUserLocation(context, fusedLocationClient)
            }
            else -> {
                // Request permission
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
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
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
            ) {
                paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            if (screenState.closestStation != null) {
                                screenState.closestStation?.stationLatestMeasurements?.let {
                                    CurrentWeatherMeasurementsInfoCard(
                                        title = stringResource(R.string.weather_at_nearest),
                                        measurements = it,
                                        elementCodelist = screenState.elementCodelist
                                    )
                                }
                            }


                            NearbyStationInfoCard(
                                title = stringResource(R.string.station_near),
                                screenState.nearbyStations,
                                onClick = navigateToDetail
                            )

                            if (screenState.allTimeRecords.isNotEmpty() && screenState.alltimeStationRecords.isNotEmpty()) {
                                val allTimeRecordData = InfoCardData(
                                    title = stringResource(R.string.records),
                                    items = screenState.allTimeRecords.mapNotNull { record ->
                                        if(record.element == "TMA" ||
                                            record.element == "Fmax" ||
                                            record.element == "SVH" ||
                                            record.element == "SNO" ||
                                            record.element == "SCE") {
                                            val elementInfo = elementAbbreviationToNameUnitPair(
                                                record.element,
                                                screenState.elementCodelist
                                            )
                                            if (elementInfo != null) {
                                                val valueWithUnit =
                                                    "${record.highest?.value} ${elementInfo.unit}"
                                                elementInfo.name to valueWithUnit
                                            } else {
                                                null
                                            }
                                        }
                                        else if(record.element == "TMI") {
                                            val elementInfo = elementAbbreviationToNameUnitPair(
                                                record.element,
                                                screenState.elementCodelist
                                            )
                                            if (elementInfo != null) {
                                                val valueWithUnit =
                                                    "${record.lowest?.value} ${elementInfo.unit}"
                                                elementInfo.name to valueWithUnit
                                            } else {
                                                null
                                            }
                                        }
                                        else {
                                            val elementInfo = elementAbbreviationToNameUnitPair(
                                                record.element,
                                                screenState.elementCodelist
                                            )
                                            if (elementInfo != null) {
                                                val valueWithUnit =
                                                    "${String.format("%.2f", record.average)} ${elementInfo.unit}"
                                                elementInfo.name to valueWithUnit
                                            } else {
                                                null
                                            }
                                        }

                                    }
                                )


                                val allTimeStationData = InfoCardData(
                                    title = stringResource(R.string.records_at_station),
                                    items = screenState.alltimeStationRecords.mapNotNull { record ->
                                        if(record.element == "TMA" ||
                                            record.element == "Fmax" ||
                                            record.element == "SVH" ||
                                            record.element == "SNO" ||
                                            record.element == "SCE") {
                                            val elementInfo = elementAbbreviationToNameUnitPair(
                                                record.element,
                                                screenState.elementCodelist
                                            )
                                            if (elementInfo != null) {
                                                val valueWithUnit =
                                                    "${record.highest?.value} ${elementInfo.unit}"
                                                elementInfo.name to valueWithUnit
                                            } else {
                                                null
                                            }
                                        }
                                        else if(record.element == "TMI") {
                                            val elementInfo = elementAbbreviationToNameUnitPair(
                                                record.element,
                                                screenState.elementCodelist
                                            )
                                            if (elementInfo != null) {
                                                val valueWithUnit =
                                                    "${record.lowest?.value} ${elementInfo.unit}"
                                                elementInfo.name to valueWithUnit
                                            } else {
                                                null
                                            }
                                        }
                                        else {
                                            val elementInfo = elementAbbreviationToNameUnitPair(
                                                record.element,
                                                screenState.elementCodelist
                                            )
                                            if (elementInfo != null) {
                                                val valueWithUnit =
                                                    "${record.average} ${elementInfo.unit}"
                                                elementInfo.name to valueWithUnit
                                            } else {
                                                null
                                            }
                                        }

                                    }
                                )
                                SwipeableInfoCard(
                                    infoCards = listOf(allTimeStationData,allTimeRecordData)
                                )

                            }
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


fun pythagoreanDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dx = lat2 - lat1
    val dy = lon2 - lon1
    return sqrt(dx. pow(2) + dy.pow(2))
}


@Composable
fun CurrentWeatherMeasurementsInfoCard(
    title: String,
    measurements: List<MeasurementLatest>,
    elementCodelist: List<ElementCodelistItem>,
    modifier: Modifier = Modifier
) {
    InfoCard(
        title = title,
        items = measurements.mapNotNull { measurement ->
            val elementInfo = elementAbbreviationToNameUnitPair(
                measurement.element,
                elementCodelist
            )
            if (elementInfo != null) {
                val valueWithUnit = "${measurement.value} ${elementInfo.unit}"
                elementInfo.name to valueWithUnit
            } else {
                null
            }
        },
        modifier = modifier
    )
}