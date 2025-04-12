package cz.cvut.weatherforge.features.home.presentation

import InfoCard
import InfoCardData
import NearbyStationInfoCard
import SwipeableInfoCard
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.core.utils.elementAbbreviationToNameUnitPair
import cz.cvut.weatherforge.core.utils.getLocalizedDateString
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementLatest
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale


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
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = stringResource(R.string.nearest_station),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        screenState.closestStation?.let {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        navigateToDetail(screenState.closestStation!!.stationId)
                                    },
                                text = screenState.closestStation!!.location,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState())
                ) {
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
                                    elementCodelist = screenState.elementCodelist,
                                )
                            }
                        }

                        if (screenState.alltimeStationRecords.isNotEmpty()) {
                                                                    val allTimeStationData = InfoCardData(
                                            title = stringResource(R.string.records_at_station),
                                            items = screenState.alltimeStationRecords.mapNotNull { record ->
                                                if (record.element == "TMA" ||
                                                    record.element == "Fmax" ||
                                                    record.element == "SVH" ||
                                                    record.element == "SNO" ||
                                                    record.element == "SRA" ||
                                                    record.element == "SCE"
                                                ) {
                                                    val elementInfo = elementAbbreviationToNameUnitPair(
                                                        record.element,
                                                        screenState.elementCodelist
                                                    )
                                                    if (elementInfo != null) {
                                                        val valueWithUnit =
                                                            "${record.highest?.value} ${elementInfo.unit} (${getLocalizedDateString(
                                                                record.highest?.recordDate?.toJavaLocalDate()
                                                            )})"
                                                        elementInfo.name to valueWithUnit
                                                    } else {
                                                        null
                                                    }
                                                } else {
                                                    val elementInfo = elementAbbreviationToNameUnitPair(
                                                        record.element,
                                                        screenState.elementCodelist
                                                    )
                                                    if (elementInfo != null) {
                                                        val valueWithUnit =
                                                            "${record.lowest?.value} ${elementInfo.unit} (${getLocalizedDateString(
                                                                record.lowest?.recordDate?.toJavaLocalDate()
                                                            )})"
                                                        elementInfo.name to valueWithUnit
                                                    } else {
                                                        null
                                                    }
                                                }
                                            }
                                        )
                                        InfoCard(
                                            title = allTimeStationData.title,
                                            items = allTimeStationData.items,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                        NearbyStationInfoCard(
                            title = stringResource(R.string.station_near),
                            screenState.nearbyStations,
                            onClick = navigateToDetail
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
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


@Composable
fun CurrentWeatherMeasurementsInfoCard(
    title: String,
    measurements: List<MeasurementLatest>,
    elementCodelist: List<ElementCodelistItem>,
    modifier: Modifier = Modifier,
) {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withLocale(Locale("cs", "CZ"))
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
        footer = "${stringResource(R.string.updated)} ${measurements.first().timestamp.toJavaLocalDateTime().plusHours(2).format(formatter)}",
        modifier = modifier
    )
}
