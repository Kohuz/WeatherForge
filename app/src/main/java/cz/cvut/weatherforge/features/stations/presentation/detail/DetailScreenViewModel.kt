package cz.cvut.weatherforge.features.stations.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailScreenViewModel(
    private val repository: StationRepository
) : ViewModel() {
    private val _screenStateStream = MutableStateFlow(DetailScreenState())
    val screenStateStream get() = _screenStateStream.asStateFlow()

    data class DetailScreenState(
        val station: Station? = null,
        val selectedTabIndex: Int = 0,
        val elementCodelist: List<ElementCodelistItem> = emptyList(),
    )

    init {
        viewModelScope.launch {
            val elementCodelistResult = repository.getElementsCodelist()
            if(elementCodelistResult.isSuccess){
                _screenStateStream.update { it.copy(elementCodelist = elementCodelistResult.elements) }
            }
        }
    }
    fun loadStation(stationId: String) {
        viewModelScope.launch {
            val station = repository.getStation(stationId)
            _screenStateStream.update { it.copy(station = station.station) }
        }
    }

    fun selectTab(index: Int) {
        _screenStateStream.update { it.copy(selectedTabIndex = index) }
    }
}

fun elementAbbreviationToNameUnitPair(abbreviation: String, codelist: List<ElementCodelistItem>): ElementCodelistItem? {
    return codelist.find { item -> item.abbreviation == abbreviation }
}
