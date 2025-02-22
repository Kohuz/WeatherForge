package cz.cvut.weatherforge.features.stations.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.weatherforge.features.stations.data.Station
import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.StationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListScreenViewModel(private val repository: StationRepository): ViewModel() {
    private val _screenStateStream = MutableStateFlow(ListScreenState())
    val screenStateStream get() = _screenStateStream.asStateFlow()
    data class ListScreenState(
        val results: List<Station> = emptyList(),
        val loading: Boolean = false,
        val successful: Boolean = true,
        val currentQuery: String = "",
        val currentFilter: Filter = Filter.All,
        val dialogOpen: Boolean = false
    )
    enum class Filter{
        Active,
        Inactive,
        All
    }
    fun onFilterChange(filter: Filter){
        _screenStateStream.update { state ->
            state.copy(currentFilter = filter)
        }
        onEntriesSearched()
    }

    fun onQueryChange(query: String){
        _screenStateStream.update { state ->
            state.copy(currentQuery=query)
        }
    }

    fun onEntriesSearched(){
        val currentFilter = _screenStateStream.value.currentFilter
        if (screenStateStream.value.currentQuery == ""){
            _screenStateStream.update { state ->
                state.copy( results = emptyList())
            }
            return
        }
        viewModelScope.launch {
            //Fetch entries
            val result: StationResult =
                when(currentFilter){
                    Filter.Active ->  repository.getStations(name= screenStateStream.value.currentQuery, active = true)
                    Filter.Inactive -> repository.getStations(name= screenStateStream.value.currentQuery, active = false)
                    else -> repository.getStations(name= screenStateStream.value.currentQuery)
                }
            //Request failed
            if(!result.isSuccess){
                _screenStateStream.update { state ->
                    state.copy(successful = false, dialogOpen = true, loading = false)
                }
            }
            //Request successful
            else {
                val sortedResultByLocation= result.stations.sortedBy { it.location}
                _screenStateStream.update { state ->
                    state.copy(results = sortedResultByLocation, loading = false, dialogOpen = false)
                }

            }
        }
    }
    fun onDialogClose() {
        _screenStateStream.update { state ->
            state.copy(dialogOpen = false)
        }
    }

    fun onDialogCloseRetry() {
        _screenStateStream.update { state ->
            state.copy(dialogOpen = false, loading = true)
        }
        onEntriesSearched()
    }
}
