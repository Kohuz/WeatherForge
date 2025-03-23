package cz.cvut.weatherforge.features.record.presentatioin

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import cz.cvut.weatherforge.core.utils.calculateDistancesForNearbyStations
import cz.cvut.weatherforge.features.measurements.data.MeasurementRepository
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDailyResult
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementLatest
import cz.cvut.weatherforge.features.record.data.RecordRepository
import cz.cvut.weatherforge.features.record.data.model.RecordStats
import cz.cvut.weatherforge.features.record.data.model.StationRecord
import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.data.model.StationElement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecordsScreenViewModel(
    private val stationRepository: StationRepository,
    private val recordRepository: RecordRepository,
    private val measurementRepository: MeasurementRepository,
) : ViewModel() {

    private val _screenStateStream = MutableStateFlow(RecordsScreenState())
    val screenStateStream get() = _screenStateStream.asStateFlow()

    data class RecordsScreenState(
        val elementCodelist: List<ElementCodelistItem> = emptyList(),
        val allStations: List<Station> = emptyList(),
        val filteredStations: List<Station> = emptyList(),
        val allTimeRecords: List<RecordStats> = emptyList(),
        val selectedElement: ElementCodelistItem? = null,
        val selectedStation: Station? = null,
        val selectedStationName: String? = null,
        val selectedDate: String? = null,
        val showDatePicker: Boolean = false,
        val loading: Boolean = false,
        val measurements: List<MeasurementDaily> = emptyList(),
        val searchQuery: String = "", // Added for station search query
        val selectedOption: String = "Date", // Added for selected option (Date or Station)
        val expanded: Boolean = false // Added for dropdown visibility
    )

    init {
        viewModelScope.launch {
            // Load elements codelist
            val elementCodelistResult = stationRepository.getElementsCodelist()
            if (elementCodelistResult.isSuccess) {
                _screenStateStream.update { it.copy(elementCodelist = elementCodelistResult.elements) }
            }

            // Load all stations
            setLoadingState(true)
            val stationsResult = stationRepository.getStations()
            if (stationsResult.isSuccess) {
                _screenStateStream.update { state ->
                    state.copy(
                        allStations = stationsResult.stations,
                        filteredStations = stationsResult.stations // Initially, show all stations
                    )
                }
            }
            setLoadingState(false)
        }
    }

    // Method to filter stations based on search query
    fun filterStations(query: String) {
        _screenStateStream.update { state ->
            val filteredStations = if (query.isBlank()) {
                state.allStations // Show all stations if query is empty
            } else {
                state.allStations.filter { station ->
                    station.location.startsWith(query, ignoreCase = true) // Case-insensitive search
                }
            }
            state.copy(filteredStations = filteredStations)
        }
    }

    fun selectStation(station: Station) {
        _screenStateStream.update { state ->
            state.copy(
                selectedStation = station,
                selectedStationName = station.location
            )
        }
    }
    fun loadInfo() {
        viewModelScope.launch {
            setLoadingState(true)
            fetchAllTimeRecords()
            setLoadingState(false)
        }
    }

    private fun fetchAllTimeRecords() {
        viewModelScope.launch {
            val allTimeRecordsResult = recordRepository.getAllTimeRecords()
            if (allTimeRecordsResult.isSuccess) {
                _screenStateStream.update { state ->
                    state.copy(allTimeRecords = allTimeRecordsResult.stats)
                }
            }
        }
    }

    // Method to select an element
    fun selectElement(element: ElementCodelistItem) {
        _screenStateStream.update { state ->
            state.copy(selectedElement = element)
        }
    }

    // Method to set the selected station name
    fun setSelectedStationName(name: String) {
        _screenStateStream.update { state ->
            state.copy(selectedStationName = name)
        }
    }

    // Method to show/hide the date picker
    fun showDatePicker(show: Boolean) {
        _screenStateStream.update { state ->
            state.copy(showDatePicker = show)
        }
    }

    // Method to set the selected date
    fun setSelectedDate(date: String) {
        _screenStateStream.update { state ->
            state.copy(selectedDate = date)
        }
    }

    fun setSearchQuery(query: String) {
        _screenStateStream.update { state ->
            state.copy(searchQuery = query)
        }
    }

    fun setSelectedOption(option: String) {
        _screenStateStream.update { state ->
            state.copy(selectedOption = option)
        }
    }

    fun setExpanded(expanded: Boolean) {
        _screenStateStream.update { state ->
            state.copy(expanded = expanded)
        }
    }

    // Method to fetch measurements based on selected element, station, and date
    fun fetchMeasurements() {
        viewModelScope.launch {
            val selectedElement = _screenStateStream.value.selectedElement
            val selectedStation = _screenStateStream.value.selectedStation
            val selectedDate = _screenStateStream.value.selectedDate

            if (selectedElement != null && (selectedStation != null || selectedDate != null)) {
                setLoadingState(true)

                var result: MeasurementDailyResult
                if (screenStateStream.value.selectedOption == "Date") {
                    result = measurementRepository.getMeasurementsTop(
                        null,
                        selectedDate,
                        selectedElement.abbreviation
                    )
                }
                else {
                    result = measurementRepository.getMeasurementsTop(
                        selectedStation!!.stationId,
                        null,
                        selectedElement.abbreviation
                    )
                }

                if (result.isSuccess) {
                    _screenStateStream.update { state ->
                        state.copy(measurements = result.measurements)
                    }
                }
                setLoadingState(false)
            }
        }
    }

    // Method to set the loading state
    private suspend fun setLoadingState(isLoading: Boolean) {
        _screenStateStream.update { state ->
            state.copy(loading = isLoading)
        }
    }
}