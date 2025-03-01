package cz.cvut.weatherforge.features.home.presentation

import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import org.koin.androidx.compose.koinViewModel


@Composable
fun HomeScreen( viewModel: HomeScreenViewModel = koinViewModel()) {
    val context = LocalContext.current
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

    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()

    when (screenState.loading) {
        false -> {
            Scaffold(
                topBar = {

                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        HorizontalDivider()
                        screenState.closestStation?.let {
                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = it.location,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
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






