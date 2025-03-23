package cz.cvut.weatherforge.features.stations.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import cz.cvut.weatherforge.core.utils.calculateDistancesForNearbyStations
import cz.cvut.weatherforge.features.measurements.data.MeasurementRepository
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementMonthly
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementYearly
import cz.cvut.weatherforge.features.record.data.RecordRepository
import cz.cvut.weatherforge.features.record.data.model.RecordStats
import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DetailScreenViewModel(
    private val stationRepository: StationRepository, private val recordRepository: RecordRepository,
    private val measurementRepository: MeasurementRepository) : ViewModel() {
    private val _screenStateStream = MutableStateFlow(DetailScreenState())
    val screenStateStream get() = _screenStateStream.asStateFlow()

    data class DetailScreenState(
        val station: Station? = null,
        val selectedTabIndex: Int = 0,
        val nearbyStations: List<Pair<Station, Double>> = emptyList(),
        val elementCodelist: List<ElementCodelistItem> = emptyList(),
        val allTimeRecords: List<RecordStats> = emptyList(),
        val dailyMeasurements: List<MeasurementDaily> = emptyList(),
        val monthlyMeasurements: List<MeasurementMonthly> = emptyList(),
        val yearlyMeasurements: List<MeasurementYearly> = emptyList()
    )

    init {
        viewModelScope.launch {
            val elementCodelistResult = stationRepository.getElementsCodelist()
            if(elementCodelistResult.isSuccess){
                _screenStateStream.update { it.copy(elementCodelist = elementCodelistResult.elements) }
            }
            if(screenStateStream.value.station != null) {
                fetchNearbyStations(
                    screenStateStream.value.station!!.latitude,
                    screenStateStream.value.station!!.longitude
                )
            }

        }
    }
    fun loadStation(stationId: String) {
        runBlocking {
            val station = stationRepository.getStation(stationId)
            _screenStateStream.update { it.copy(station = station.station) }
        }
    }

    fun selectTab(index: Int) {
        _screenStateStream.update { it.copy(selectedTabIndex = index) }
    }

    private suspend fun fetchNearbyStations(latitude: Double, longitude: Double) {
        val nearbyStationsResult = stationRepository.getNearbyStations(
            latitude,
            longitude
        )

        if (nearbyStationsResult.isSuccess) {
            val nearbyStationsWithDistance = calculateDistancesForNearbyStations(
                nearbyStationsResult.stations,
                LatLng(latitude, longitude)
            )
            updateNearbyStations(nearbyStationsWithDistance)
        }
    }

    private fun updateNearbyStations(nearbyStations: List<Pair<Station, Double>>) {
        _screenStateStream.update { state ->
            state.copy(nearbyStations = nearbyStations)
        }
    }
    fun loadRecords() {
        viewModelScope.launch {
            val allTimeRecordsResult =
                recordRepository.getAllTimeStationRecords(screenStateStream.value.station!!.stationId)
            if (allTimeRecordsResult.isSuccess) {
                _screenStateStream.update { it.copy(allTimeRecords = allTimeRecordsResult.stats) }
            }
        }
    }

    fun fetchDailyMeasurements(stationId: String, dateFrom: String, dateTo: String, element: String) {
        viewModelScope.launch {
            val dailyMeasurementsResult = measurementRepository.getDailyMeasurements(stationId,dateFrom, dateTo, element)
            if (dailyMeasurementsResult.isSuccess) {
                _screenStateStream.update { it.copy(dailyMeasurements = dailyMeasurementsResult.measurements) }
            }
        }
    }

    fun fetchMonthlyMeasurements(stationId: String, dateFrom: String, dateTo: String, element: String) {
        viewModelScope.launch {
            val monthlyMeasurementsResult = measurementRepository.getMonthlyMeasurements(stationId,dateFrom, dateTo, element)
            if (monthlyMeasurementsResult.isSuccess) {
                _screenStateStream.update { it.copy(monthlyMeasurements = monthlyMeasurementsResult.measurements) }
            }
        }
    }

    fun fetchYearlyMeasurements(stationId: String, dateFrom: String, dateTo: String, element: String) {
        viewModelScope.launch {
            val yearlyMeasurementsResult = measurementRepository.getYearlyMeasurements(stationId,dateFrom, dateTo, element)
            if (yearlyMeasurementsResult.isSuccess) {
                _screenStateStream.update { it.copy(yearlyMeasurements = yearlyMeasurementsResult.measurements) }
            }
        }
    }

    fun toggleFavorite(stationId: String) {
        viewModelScope.launch {
            val station = screenStateStream.value.station
            station?.let {
                if (it.isFavorite) {
                    stationRepository.removeFavorite(stationId)
                } else {
                    stationRepository.makeFavorite(stationId)
                }
                loadStation(stationId)
            }
        }
    }
}

fun elementAbbreviationToNameUnitPair(abbreviation: String, codelist: List<ElementCodelistItem>): ElementCodelistItem? {
    return codelist.find { item -> item.abbreviation == abbreviation }
}
