package cz.cvut.weatherforge.features.stations.presentation.map

import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(navigateToDetail: (id: String) -> Unit, viewModel: MapScreenViewModel = koinViewModel()) {
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()
    val results = screenState.results
    val cameraPositionState = rememberCameraPositionState()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val userLocation = screenState.userLocation
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                rotationGesturesEnabled = true,
                compassEnabled = false,
                myLocationButtonEnabled = true
            )
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Fetch the user's location and update the camera if permission is granted
            viewModel.fetchUserLocation(context, fusedLocationClient)
        } else {
            // Handle the case when permission is denied
            Log.e("locationScreen", "Location permission was denied by the user.")
        }
    }

// Request the location permission when the composable is launched
    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            // Check if the location permission is already granted
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Fetch the user's location and update the camera
                viewModel.fetchUserLocation(context, fusedLocationClient)
            }
            else -> {
                // Request the location permission if it has not been granted
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings

    ){
        userLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 8f)
        }

        Clustering(items = results,
            onClusterClick = { cluster ->
                coroutineScope.launch {
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
                navigateToDetail(marker.stationId)
                false
            },
            clusterItemContent = { content ->
                    Icon(Icons.Filled.Place, contentDescription = content.location)
            }
            )
    }
}

