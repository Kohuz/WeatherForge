package cz.cvut.weatherforge.features.home.presentation

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import cz.cvut.weatherforge.features.measurements.data.model.StationRecord
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.model.StationsResult
import cz.cvut.weatherforge.features.stations.data.model.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val repository: StationRepository) : ViewModel() {
    private val _screenStateStream = MutableStateFlow(HomeScreenState())
    val screenStateStream get() = _screenStateStream.asStateFlow()

    data class HomeScreenState(
        val closestStation: Station? = null,
        val nearbyStations: List<Station> = emptyList(),
        val todayRecords: List<StationRecord> = emptyList(),
        val longTermRecords: List<StationRecord> = emptyList(),
        val loading: Boolean = false,
        val successful: Boolean = true,
        val userLocation: LatLng? = null
    )

    fun loadInfo() {
        viewModelScope.launch {
            _screenStateStream.update { state ->
                state.copy(loading = true)
            }

            // Fetch the user's location
            //fetchUserLocation(context, fusedLocationClient)

            // Get the user's location from the state
            val userLocation = _screenStateStream.value.userLocation

            if (userLocation != null) {
                // Fetch the closest station
                val closestStationResult = repository.getClosestStation(userLocation.latitude.toFloat(), userLocation.longitude.toFloat())
                if (closestStationResult.isSuccess) {
                    _screenStateStream.update { state ->
                        state.copy(closestStation = closestStationResult.station)
                    }
                }

                // Fetch nearby stations
                val nearbyStationsResult = repository.getNearbyStations(userLocation.latitude.toFloat(), userLocation.longitude.toFloat())
                if (nearbyStationsResult.isSuccess) {
                    _screenStateStream.update { state ->
                        state.copy(nearbyStations = nearbyStationsResult.stations)
                    }
                }
            }

            // Update loading state
            _screenStateStream.update { state ->
                state.copy(loading = false)
            }
        }
    }

    fun fetchUserLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                // Fetch the last known location
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        // Update the user's location in the state
                        val userLatLng = LatLng(it.latitude, it.longitude)
                        _screenStateStream.update { state ->
                            state.copy(userLocation = userLatLng)
                        }
                        loadInfo()
                    }
                }
            } catch (e: SecurityException) {
                Log.e("location", "Permission for location access was revoked: ${e.localizedMessage}")
            }
        } else {
            Log.e("location", "Location permission is not granted.")
        }
    }
}