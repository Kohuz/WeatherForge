package cz.cvut.weatherforge.features.home.presentation

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementLatest
import cz.cvut.weatherforge.features.record.data.RecordRepository
import cz.cvut.weatherforge.features.record.data.model.RecordStats
import cz.cvut.weatherforge.features.record.data.model.StationRecord
import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val stationRepository: StationRepository, private val recordRepository: RecordRepository) : ViewModel() {
    private val _screenStateStream = MutableStateFlow(HomeScreenState())
    val screenStateStream get() = _screenStateStream.asStateFlow()

    data class HomeScreenState(
        val closestStation: Station? = null,
        val nearbyStations: List<Pair<String, String>> = emptyList(),
        val elementCodelist: List<ElementCodelistItem> = emptyList(),
        val actualMeasurements: List<MeasurementLatest> = emptyList(),
        val todayStationRecords: List<StationRecord> = emptyList(),
        val todayAlltimeRecords: List<StationRecord> = emptyList(),
        val alltimeStationRecords: List<RecordStats> = emptyList(),
        val allTimeRecords: List<RecordStats> = emptyList(),
        val loading: Boolean = false,
        val successful: Boolean = true,
        val userLocation: LatLng? = null
    )

    init {
        viewModelScope.launch {
            val elementCodelistResult = stationRepository.getElementsCodelist()
            if(elementCodelistResult.isSuccess){
                _screenStateStream.update { it.copy(elementCodelist = elementCodelistResult.elements) }
            }
        }
    }

    fun loadInfo() {
        viewModelScope.launch {
            setLoadingState(true)
            val userLocation = _screenStateStream.value.userLocation

            if (userLocation != null) {
                fetchClosestStation(userLocation)
                fetchNearbyStations(userLocation)
            }

            setLoadingState(false)
        }
    }

    private suspend fun fetchClosestStation(userLocation: LatLng) {
        val closestStationResult = stationRepository.getClosestStation(
            userLocation.latitude.toFloat(),
            userLocation.longitude.toFloat()
        )

        if (closestStationResult.isSuccess) {
            updateClosestStation(closestStationResult.station)
            closestStationResult.station?.let { station ->
                fetchAllTimeRecords(station.stationId)
            }
        }
    }

    private suspend fun updateClosestStation(station: Station?) {
        _screenStateStream.update { state ->
            state.copy(closestStation = station)
        }
    }

    private suspend fun fetchAllTimeRecords(stationId: String) {
        val statsResult = recordRepository.getAllTimeStationRecords(stationId)
        if (statsResult.isSuccess) {
            _screenStateStream.update { state ->
                state.copy(allTimeRecords = statsResult.stats)
            }
        }
    }

    private suspend fun fetchNearbyStations(userLocation: LatLng) {
        val nearbyStationsResult = stationRepository.getNearbyStations(
            userLocation.latitude.toFloat(),
            userLocation.longitude.toFloat()
        )

        if (nearbyStationsResult.isSuccess) {
            val nearbyStationsWithDistance = calculateDistancesForNearbyStations(
                nearbyStationsResult.stations,
                userLocation
            )
            updateNearbyStations(nearbyStationsWithDistance)
        }
    }

    private fun calculateDistancesForNearbyStations(
        stations: List<Station>,
        userLocation: LatLng
    ): List<Pair<String, String>> {
        return stations.drop(1).map { station ->
            val distance = pythagoreanDistance(
                userLocation.latitude,
                userLocation.longitude,
                station.latitude,
                station.longitude
            ) * 111.32 // kilometer per degree
            station.location to "%.2f km".format(distance)
        }
    }

    private suspend fun updateNearbyStations(nearbyStations: List<Pair<String, String>>) {
        _screenStateStream.update { state ->
            state.copy(nearbyStations = nearbyStations)
        }
    }

    private suspend fun setLoadingState(isLoading: Boolean) {
        _screenStateStream.update { state ->
            state.copy(loading = isLoading)
        }
    }

    private val defaultLocation = LatLng(50.0755, 14.4378) // Default to Prague, for example

    fun fetchUserLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    val userLatLng = location?.let {
                        LatLng(it.latitude, it.longitude)
                    } ?: defaultLocation // Use default location if location is null

                    _screenStateStream.update { state ->
                        state.copy(userLocation = userLatLng)
                    }
                    loadInfo()
                }.addOnFailureListener { e ->
                    Log.e("locc", "Failed to fetch location: ${e.localizedMessage}")
                    _screenStateStream.update { state ->
                        state.copy(userLocation = defaultLocation)
                    }
                    loadInfo()
                }
            } catch (e: SecurityException) {
                Log.w("locc", "Permission for location access was revoked: ${e.localizedMessage}")
                _screenStateStream.update { state ->
                    state.copy(userLocation = defaultLocation)
                }
                loadInfo()
            }
        } else {
            Log.e("locc", "Location permission is not granted.")
            _screenStateStream.update { state ->
                state.copy(userLocation = defaultLocation)
            }
            loadInfo()
        }
    }
}