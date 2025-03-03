package cz.cvut.weatherforge.features.stations.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.weatherforge.features.record.data.RecordRepository
import cz.cvut.weatherforge.features.record.data.model.RecordStats
import cz.cvut.weatherforge.features.record.data.model.StatsResult
import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DetailScreenViewModel(
    private val stationRepository: StationRepository, private val recordRepository: RecordRepository
) : ViewModel() {
    private val _screenStateStream = MutableStateFlow(DetailScreenState())
    val screenStateStream get() = _screenStateStream.asStateFlow()

    data class DetailScreenState(
        val station: Station? = null,
        val selectedTabIndex: Int = 0,
        val elementCodelist: List<ElementCodelistItem> = emptyList(),
        val allTimeRecords: List<RecordStats> = emptyList(),
        val selectedResolutionIndex: Int = 0,
        val expanded: Boolean = false,
        val selectedElement: ElementCodelistItem? = null
    )

    init {
        viewModelScope.launch {
            val elementCodelistResult = stationRepository.getElementsCodelist()
            if(elementCodelistResult.isSuccess){
                _screenStateStream.update { it.copy(elementCodelist = elementCodelistResult.elements) }
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

    fun selectResolution(resolutionIndex: Int) {
        _screenStateStream.update { it.copy(selectedResolutionIndex = resolutionIndex) }
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

    fun toggleDropdown(expanded: Boolean) {
        _screenStateStream.update { it.copy(expanded = expanded) }
    }

    fun selectElement(element: ElementCodelistItem) {
        _screenStateStream.update { it.copy(selectedElement = element) }
    }
}

fun elementAbbreviationToNameUnitPair(abbreviation: String, codelist: List<ElementCodelistItem>): ElementCodelistItem? {
    return codelist.find { item -> item.abbreviation == abbreviation }
}
