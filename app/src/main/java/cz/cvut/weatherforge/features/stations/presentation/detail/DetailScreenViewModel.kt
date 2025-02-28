package cz.cvut.weatherforge.features.stations.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.weatherforge.features.stations.data.StationRepository
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

    fun loadStation(stationId: String) {
        viewModelScope.launch {
            val station = repository.getStation(stationId)
            _screenStateStream.update { it.copy(station = station.station) }
        }
    }
}

data class DetailScreenState(val station: Station? = null)