package cz.cvut.weatherforge.features.stations.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.model.StationsResult
import cz.cvut.weatherforge.features.stations.data.model.isActive
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
        val sortingCriteria: String = "Alphabetical",
        val ascendingOrder: Boolean = true,
        val dialogOpen: Boolean = false
    )

    enum class Filter {
        Active,
        Inactive,
        All,
        Favorites
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
            val result: StationsResult = repository.getStations()

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

    fun setSortingCriteria(sortingCriteria: String) {
        _screenStateStream.update { state ->
            state.copy(sortingCriteria = sortingCriteria)
        }
        applyFilterAndSearch()
    }

    fun setAscendingOrder(ascendingOrder: Boolean) {
        _screenStateStream.update { state ->
            state.copy(ascendingOrder = ascendingOrder)
        }
        applyFilterAndSearch()
    }

    private fun applyFilterAndSearch() {
        val currentFilter = _screenStateStream.value.currentFilter
        val currentQuery = _screenStateStream.value.currentQuery
        val sortingCriteria = _screenStateStream.value.sortingCriteria
        val ascendingOrder = _screenStateStream.value.ascendingOrder

        // Step 1: Filter by active/inactive/favorites
        val filteredByStatus = when (currentFilter) {
            Filter.Active -> allStations.filter { it.isActive() }
            Filter.Inactive -> allStations.filter { !it.isActive() }
            Filter.Favorites -> allStations.filter { it.isFavorite } // Add this line
            else -> allStations
        }

        // Step 2: Filter by search query
        val filteredByQuery = if (currentQuery.isBlank()) {
            filteredByStatus
        } else {
            filteredByStatus.filter { it.location.startsWith(currentQuery, ignoreCase = true) }
        }

        // Step 3: Sort the filtered results
        val sortedResults = when (sortingCriteria) {
            "Elevation" -> sortStationsByElevation(filteredByQuery, ascendingOrder)
            "Begin Date" -> sortStationsByBeginDate(filteredByQuery, ascendingOrder)
            "Alphabetical" -> sortStationsAlphabetically(filteredByQuery, ascendingOrder)
            else -> filteredByQuery
        }

        // Update the state with the final results
        _screenStateStream.update { state ->
            state.copy(results = sortedResults, loading = false, dialogOpen = false)
        }
    }

    private fun sortStationsByElevation(stations: List<Station>, ascending: Boolean): List<Station> {
        return if (ascending) {
            stations.sortedBy { it.elevation }
        } else {
            stations.sortedByDescending { it.elevation }
        }
    }

    private fun sortStationsByBeginDate(stations: List<Station>, ascending: Boolean): List<Station> {
        return if (ascending) {
            stations.sortedBy { it.startDate }
        } else {
            stations.sortedByDescending { it.startDate }
        }
    }

    private fun sortStationsAlphabetically(stations: List<Station>, ascending: Boolean): List<Station> {
        return if (ascending) {
            stations.sortedBy { it.location }
        } else {
            stations.sortedByDescending { it.location }
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


    fun toggleFavorite(stationId: String) {
        viewModelScope.launch {
            val station = allStations.find { it.stationId == stationId }
            station?.let {
                if (it.isFavorite) {
                    repository.removeFavorite(stationId)
                } else {
                repository.makeFavorite(stationId)
                }
                // Reload stations to reflect the updated favorite status
                loadStations()
            }
        }
    }
}