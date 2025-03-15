package cz.cvut.weatherforge.features.stations.presentation.map

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.data.model.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class StationFilter {
    ALL, ACTIVE, INACTIVE
}
class MapScreenViewModel(private val repository: StationRepository) : ViewModel()  {
    private val _screenStateStream = MutableStateFlow(MapScreenState())
    val screenStateStream get() = _screenStateStream.asStateFlow()

    // Store the full list of stations
    private var allStations: List<Station> = emptyList()

    data class MapScreenState(
        val results: List<Station> = emptyList(),
        val loading: Boolean = false,
        val successful: Boolean = true,
        val userLocation: LatLng? = null,
        val stationFilter: StationFilter = StationFilter.ACTIVE,

    )

    init {
        loadStations()
    }

    private fun loadStations() {

        viewModelScope.launch {
            _screenStateStream.update { state ->
                state.copy(loading = true)
            }

            val result = repository.getStations()

            if (!result.isSuccess) {
                _screenStateStream.update { state ->
                    state.copy(successful = false, loading = false)
                }
            } else {
                allStations = result.stations
                _screenStateStream.update { state ->
                    state.copy(results = allStations, loading = false)
                }
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
                    }
                }
            } catch (e: SecurityException) {
                Log.e("location", "Permission for location access was revoked: ${e.localizedMessage}")
            }
        } else {
            Log.e("location", "Location permission is not granted.")
        }
    }

    fun updateStationFilter(filter: StationFilter) {
        _screenStateStream.update { state ->
            val filteredResults = when (filter) {
                StationFilter.ALL -> allStations
                StationFilter.ACTIVE -> allStations.filter { it.isActive() }
                StationFilter.INACTIVE -> allStations.filter { !it.isActive() }
            }
            state.copy(results = filteredResults, stationFilter = filter)
        }
    }
}