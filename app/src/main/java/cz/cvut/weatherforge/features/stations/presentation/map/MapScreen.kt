package cz.cvut.weatherforge.features.stations.presentation.map

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.clustering.Clustering
import cz.cvut.weatherforge.features.stations.data.model.ClusterStation
import cz.cvut.weatherforge.features.stations.data.model.Station
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(navigateToDetail: (id: String) -> Unit, viewModel: MapScreenViewModel = koinViewModel()) {
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()
    val results = screenState.results
    val cameraPositionState = rememberCameraPositionState()


    val context = LocalContext.current
    val userLocation by viewModel.userLocation
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }


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
        cameraPositionState = cameraPositionState
    ){
        // If the user's location is available, place a marker on the map
        userLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 10f)
        }
//
//        results.forEach { station ->
//            MarkerComposable (
//                state = MarkerState(position = LatLng(station.latitude, station.longitude)),
//                title = "GeoJSON Point",
//                snippet = "Data from backend"
//            ){
//                Icon(Icons.Filled.Person, contentDescription = null)
//            }
//        }
        Clustering(items = results,
            onClusterClick = { cluster ->
                // Handle when a cluster is clicked
                Log.d("MapScreen", "Cluster clicked: ${cluster.size} items")
                false // Return false to allow default behavior (e.g., zooming in)
            },
            onClusterItemClick = { marker ->
                navigateToDetail(marker.stationId)
                false // Return false to allow default behavior
            },
            clusterItemContent = {
                    Icon(Icons.Filled.Person, contentDescription = null)
            }

            )
    }
}

