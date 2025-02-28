package cz.cvut.weatherforge.features.stations.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.weatherforge.features.stations.data.Station
import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.StationResult
import cz.cvut.weatherforge.features.stations.data.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListScreenViewModel(private val repository: StationRepository) : ViewModel() {
    private val _screenStateStream = MutableStateFlow(ListScreenState())
    val screenStateStream get() = _screenStateStream.asStateFlow()

    // Store the full list of stations
    private var allStations: List<Station> = emptyList()

    data class ListScreenState(
        val results: List<Station> = emptyList(),
        val loading: Boolean = false,
        val successful: Boolean = true,
        val currentQuery: String = "",
        val currentFilter: Filter = Filter.All,
        val dialogOpen: Boolean = false
    )

    enum class Filter {
        Active,
        Inactive,
        All
    }

    init {
        loadStations()
    }

    private fun loadStations() {
        viewModelScope.launch {
            _screenStateStream.update { state ->
                state.copy(loading = true)
            }

            // Fetch all stations
            val result: StationResult = repository.getStations()

            // Handle the result
            if (!result.isSuccess) {
                _screenStateStream.update { state ->
                    state.copy(successful = false, dialogOpen = true, loading = false)
                }
            } else {
                // Store the full list of stations
                allStations = result.stations

                // Apply initial filtering and sorting
                applyFilterAndSearch()
            }
        }
    }

    fun onFilterChange(filter: Filter) {
        _screenStateStream.update { state ->
            state.copy(currentFilter = filter)
        }
        applyFilterAndSearch()
    }

    fun onQueryChange(query: String) {
        _screenStateStream.update { state ->
            state.copy(currentQuery = query)
        }
        applyFilterAndSearch()
    }

    private fun applyFilterAndSearch() {
        val currentFilter = _screenStateStream.value.currentFilter
        val currentQuery = _screenStateStream.value.currentQuery

        // Filter by active/inactive
        val filteredByStatus = when (currentFilter) {
            Filter.Active -> allStations.filter { it.isActive() }
            Filter.Inactive -> allStations.filter { !it.isActive() }
            else -> allStations
        }

        // Filter by search query
        val filteredByQuery = if (currentQuery.isBlank()) {
            filteredByStatus
        } else {
            filteredByStatus.filter { it.location.startsWith(currentQuery, ignoreCase = true) }
        }

        // Sort by location
        val sortedResults = filteredByQuery.sortedBy { it.location }

        // Update the state
        _screenStateStream.update { state ->
            state.copy(results = sortedResults, loading = false, dialogOpen = false)
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
        loadStations() // Retry loading stations
    }
}