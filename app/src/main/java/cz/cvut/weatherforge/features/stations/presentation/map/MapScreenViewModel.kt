package cz.cvut.weatherforge.features.stations.presentation.map

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import cz.cvut.weatherforge.features.stations.data.GeoJSONStationCollection
import cz.cvut.weatherforge.features.stations.data.GeoStationResult
import cz.cvut.weatherforge.features.stations.data.Station
import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.StationResult
import cz.cvut.weatherforge.features.stations.presentation.list.ListScreenViewModel.Filter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapScreenViewModel(private val repository: StationRepository) : ViewModel()  {
    private val _userLocation = mutableStateOf<LatLng?>(null)
    val userLocation: State<LatLng?> = _userLocation
    private val _screenStateStream = MutableStateFlow(MapScreenState())
    val screenStateStream get() = _screenStateStream.asStateFlow()

    // Store the full list of stations
    private var allStations: GeoJSONStationCollection = GeoJSONStationCollection("", emptyList())

    data class MapScreenState(
        val results: List<Station> = emptyList(),
        val loading: Boolean = false,
        val successful: Boolean = true,
    )

    init {
        loadStations()
    }

    private fun loadStations() {
        viewModelScope.launch {
            _screenStateStream.update { state ->
                state.copy(loading = true)
            }

            // Fetch all stations
            val result: GeoStationResult = repository.getGeoStations()

            // Handle the result
            if (!result.isSuccess) {
                _screenStateStream.update { state ->
                    state.copy(successful = false, loading = false)
                }
            } else {
                // Store the full list of stations
                allStations = result.stations

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
                        _userLocation.value = userLatLng
                    }
                }
            } catch (e: SecurityException) {
                Log.e("location","Permission for location access was revoked: ${e.localizedMessage}")
            }
        } else {
            Log.e("location","Location permission is not granted.")
        }
    }


}