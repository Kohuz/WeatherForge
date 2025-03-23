package cz.cvut.weatherforge.features.stations.presentation.map

import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import cz.cvut.weatherforge.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(
    navigateToDetail: (id: String) -> Unit,
    stationLocation: LatLng? = null, // Nullable station location
    viewModel: MapScreenViewModel = koinViewModel()
) {
    // Collect the screen state from the ViewModel
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()
    val results = screenState.results
    val cameraPositionState = rememberCameraPositionState()
    val coroutineScope = rememberCoroutineScope()

    // Context and location-related variables
    val context = LocalContext.current
    val userLocation = screenState.userLocation
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Map UI settings
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                rotationGesturesEnabled = true,
                compassEnabled = false,
                myLocationButtonEnabled = true
            )
        )
    }

    // Permission launcher for location access
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Fetch the user's location if permission is granted
            viewModel.fetchUserLocation(context, fusedLocationClient)
        } else {
            // Log an error if permission is denied
            Log.e("MapScreen", context.getString(R.string.location_permission_denied))
        }
    }

    // Request location permission when the composable is launched
    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Fetch the user's location if permission is already granted
                viewModel.fetchUserLocation(context, fusedLocationClient)
            }
            else -> {
                // Request location permission if not granted
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // Set the initial camera position to the station's location or user's location
    LaunchedEffect(stationLocation, userLocation) {
        val targetLocation = stationLocation ?: userLocation
        targetLocation?.let { location ->
            val cameraPosition = CameraPosition.fromLatLngZoom(location, 12f) // Adjust zoom level as needed
            cameraPositionState.position = cameraPosition
        }
    }

    // Main UI layout
    Box(modifier = Modifier.fillMaxSize()) {
        // Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings
        ) {
            // Display clustered markers for stations
            Clustering(
                items = results,
                onClusterClick = { cluster ->
                    coroutineScope.launch {
                        // Zoom in on the cluster when clicked
                        val currentZoom = cameraPositionState.position.zoom
                        val newZoom = currentZoom + 1f
                        val newCameraPosition = CameraPosition.builder()
                            .target(LatLng(cluster.position.latitude, cluster.position.longitude))
                            .zoom(newZoom)
                            .build()
                        val cameraUpdate = CameraUpdateFactory.newCameraPosition(newCameraPosition)
                        cameraPositionState.animate(cameraUpdate, 500)
                    }
                    false
                },
                onClusterItemClick = { marker ->
                    // Navigate to the station detail screen when a marker is clicked
                    navigateToDetail(marker.stationId)
                    false
                },
                clusterItemContent = { content ->
                    // Display an icon for each station (using a more visible icon)
                        Icon(Icons.Filled.LocationOn, contentDescription = content.location)
                }
            )
        }

        // Filter buttons at the bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // FilterChip to show all stations
            FilterChip(
                selected = screenState.stationFilter == StationFilter.ALL,
                onClick = { viewModel.updateStationFilter(StationFilter.ALL) },
                label = { Text(context.getString(R.string.all_stations)) }
            )

            // FilterChip to show active stations
            FilterChip(
                selected = screenState.stationFilter == StationFilter.ACTIVE,
                onClick = { viewModel.updateStationFilter(StationFilter.ACTIVE) },
                label = { Text(context.getString(R.string.active_stations)) }
            )

            // FilterChip to show inactive stations
            FilterChip(
                selected = screenState.stationFilter == StationFilter.INACTIVE,
                onClick = { viewModel.updateStationFilter(StationFilter.INACTIVE) },
                label = { Text(context.getString(R.string.inactive_stations)) }
            )
        }
    }
}