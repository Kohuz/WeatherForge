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

class RecordsScreenViewModel(
    private val stationRepository: StationRepository,
    private val recordRepository: RecordRepository,
    private val measurementRepository: MeasurementRepository,
) : ViewModel() {

    private val _screenStateStream = MutableStateFlow(HomeScreenState())
    val screenStateStream get() = _screenStateStream.asStateFlow()

    data class HomeScreenState(
        val elementCodelist: List<ElementCodelistItem> = emptyList(),
        val allTimeRecords: List<RecordStats> = emptyList(),
        val selectedElement: ElementCodelistItem? = null,
        val selectedStation: Station? = null,
        val selectedDate: String? = null,
        val loading: Boolean = false,
        val measurements: List<MeasurementDaily> = emptyList()
    )

    init {
        viewModelScope.launch {
            val elementCodelistResult = stationRepository.getElementsCodelist()
            if (elementCodelistResult.isSuccess) {
                _screenStateStream.update { it.copy(elementCodelist = elementCodelistResult.elements) }
            }
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

    fun selectElement(element: ElementCodelistItem) {
        _screenStateStream.update { state ->
            state.copy(selectedElement = element)
        }
    }

    fun selectStation(station: Station) {
        _screenStateStream.update { state ->
            state.copy(selectedStation = station)
        }
    }

    fun selectDate(date: String) {
        _screenStateStream.update { state ->
            state.copy(selectedDate = date)
        }
    }

    fun fetchMeasurements() {
        viewModelScope.launch {
            val selectedElement = _screenStateStream.value.selectedElement
            val selectedStation = _screenStateStream.value.selectedStation
            val selectedDate = _screenStateStream.value.selectedDate

            if (selectedElement != null && selectedStation != null && selectedDate != null) {
                setLoadingState(true)
                val result = measurementRepository.getMeasurementsTop(selectedStation.stationId, selectedDate, selectedElement.abbreviation)
                if (result.isSuccess) {
                    _screenStateStream.update { state ->
                        state.copy(measurements = result.measurements)
                    }
                }
                setLoadingState(false)
            }
        }
    }

    private suspend fun setLoadingState(isLoading: Boolean) {
        _screenStateStream.update { state ->
            state.copy(loading = isLoading)
        }
    }
}