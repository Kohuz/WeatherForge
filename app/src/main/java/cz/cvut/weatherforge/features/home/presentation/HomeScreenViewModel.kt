package cz.cvut.weatherforge.features.home.presentation

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import cz.cvut.weatherforge.core.utils.calculateDistancesForNearbyStations
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
import kotlin.math.abs

class HomeScreenViewModel(private val stationRepository: StationRepository, private val recordRepository: RecordRepository) : ViewModel() {
    private val _screenStateStream = MutableStateFlow(HomeScreenState())
    val screenStateStream get() = _screenStateStream.asStateFlow()
    private var lastLoadedLocation: LatLng? = null


    data class HomeScreenState(
        val closestStation: Station? = null,
        val nearbyStations: List<Pair<Station, Double>> = emptyList(),
        val elementCodelist: List<ElementCodelistItem> = emptyList(),
        val actualMeasurements: List<MeasurementLatest> = emptyList(),
        val alltimeStationRecords: List<RecordStats> = emptyList(),
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

    fun loadInfo(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val currentLocation = _screenStateStream.value.userLocation
            if (currentLocation == null ||
                (lastLoadedLocation != null &&
                        !currentLocation.isSameAs(lastLoadedLocation) &&
                        !forceRefresh)) {
                return@launch
            }

            setLoadingState(true)
            lastLoadedLocation = currentLocation // Update the last loaded location

            if (currentLocation != null) {
                fetchClosestStation(currentLocation)
                fetchNearbyStations(currentLocation)
            }
            setLoadingState(false)
        }
    }

    private suspend fun fetchClosestStation(userLocation: LatLng) {
        val closestStationResult = stationRepository.getClosestStation(
            userLocation.latitude,
            userLocation.longitude
        )

        if (closestStationResult.isSuccess) {
            updateClosestStation(closestStationResult.station)
            closestStationResult.station?.let { station ->
                fetchTodayAllTimeStationRecords(station.stationId)
            }
        }
    }

    private fun updateClosestStation(station: Station?) {
        _screenStateStream.update { state ->
            state.copy(closestStation = station)
        }
    }

    private suspend fun fetchTodayAllTimeStationRecords(stationId: String) {
        val statsResult = recordRepository.getAllTimeStationRecords(stationId)
        if (statsResult.isSuccess) {
            _screenStateStream.update { state ->
                state.copy(alltimeStationRecords = statsResult.stats)
            }
        }
    }

    private suspend fun fetchNearbyStations(userLocation: LatLng) {
        val nearbyStationsResult = stationRepository.getNearbyStations(
            userLocation.latitude,
            userLocation.longitude
        )

        if (nearbyStationsResult.isSuccess) {
            val nearbyStationsWithDistance = calculateDistancesForNearbyStations(
                nearbyStationsResult.stations,
                userLocation
            )
            updateNearbyStations(nearbyStationsWithDistance)
        }
    }

    private suspend fun updateNearbyStations(nearbyStations: List<Pair<Station, Double>>) {
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
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    val newLocation = location?.let {
                        LatLng(it.latitude, it.longitude)
                    } ?: defaultLocation

                    // Only update and load if location changed significantly
                    if (!newLocation.isSameAs(_screenStateStream.value.userLocation)) {
                        _screenStateStream.update { state ->
                            state.copy(userLocation = newLocation)
                        }

                        // Only load info if this is a new location we haven't loaded before
                        if (!newLocation.isSameAs(lastLoadedLocation)) {
                            lastLoadedLocation = newLocation
                            loadInfo()
                        }
                    }
                }.addOnFailureListener { e ->
                    Log.e("Location", "Failed to fetch location: ${e.localizedMessage}")
                    handleLocationError()
                }
            } catch (e: SecurityException) {
                Log.w("Location", "Permission revoked: ${e.localizedMessage}")
                handleLocationError()
            }
        } else {
            Log.e("Location", "Permission not granted")
            handleLocationError()
        }
    }

    private fun handleLocationError() {
        // Only update and load if we don't already have the default location
        if (!defaultLocation.isSameAs(_screenStateStream.value.userLocation)) {
            _screenStateStream.update { state ->
                state.copy(userLocation = defaultLocation)
            }
            lastLoadedLocation = defaultLocation
            loadInfo()
        }
    }
    private fun LatLng.isSameAs(other: LatLng?, threshold: Double = 0.001): Boolean {
        return other != null &&
                abs(this.latitude - other.latitude) < threshold &&
                abs(this.longitude - other.longitude) < threshold
    }
}