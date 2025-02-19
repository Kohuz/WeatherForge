package cz.cvut.weatherforge.features.stations.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cz.cvut.weatherforge.features.stations.data.StationRepository

class DetailScreenViewModel(private val savedStateHandle: SavedStateHandle,
                            private val repository: StationRepository): ViewModel() {
//    private val _screenStateStream = MutableStateFlow(DetailScreenState())
//    val screenStateStream get() = _screenStateStream.asStateFlow()
//
//    init {
//        viewModelScope.launch {
//            val stationId: String = savedStateHandle[DetailScreen.ID]
//                ?: throw NullPointerException("Station id is missing")
//            val sportEntry = repository.getSportEntry(sportEntryId)
//            _screenStateStream.update { it.copy(sportEntry = sportEntry) }
//        }
//    }
}
//data class DetailScreenState(val sportEntry: SportEntry? = null)

