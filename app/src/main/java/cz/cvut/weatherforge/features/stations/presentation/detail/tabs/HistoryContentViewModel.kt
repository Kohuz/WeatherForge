package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.weatherforge.features.measurements.data.MeasurementRepository
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDailyResult
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementMonthlyResult
import cz.cvut.weatherforge.features.measurements.data.model.ValueStatsResult
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class HistoryContentViewModel(
    private val repository: MeasurementRepository
) : ViewModel() {

    data class HistoryContentState(
        val selectedConcreteDayDate: LocalDate? = null,
        val selectedGraphDate: LocalDate? = null,
        val selectedLongTermDate: LocalDate? = null,

        val selectedResolutionIndex: Int = 0,
        val dropdownExpanded: Boolean = false,
        val selectedElement: ElementCodelistItem? = null,

        val showGraphDatePicker: Boolean = false,
        val showConcreteDayDatePicker: Boolean = false,
        val showLongTermDatePicker: Boolean = false,


        val dailyStats: ValueStatsResult? = null,
        val dailyAndMonthlyMeasurements: MeasurementDailyResult? = null,
        val monthlyMeasurements: MeasurementMonthlyResult? = null,
        val statsDay: MeasurementDailyResult? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    // State for the HistoryContent
    private val _state = MutableStateFlow(HistoryContentState())
    val historyContentState get() = _state.asStateFlow()


    fun selectResolution(resolutionIndex: Int) {
        _state.update { it.copy(selectedResolutionIndex = resolutionIndex) }
    }

    fun toggleDropdown(expanded: Boolean) {
        _state.update { it.copy(dropdownExpanded = expanded) }
    }

    fun setSelectedConcreteDayDate(date: LocalDate) {
        _state.update { it.copy(selectedConcreteDayDate = date) }
    }

    // Function to show/hide the date picker
    fun showConcreteDatePicker(show: Boolean) {
        _state.update { it.copy(showConcreteDayDatePicker = show) }
    }

    fun showGraphDatePicker(show: Boolean) {
        _state.update { it.copy(showGraphDatePicker = show) }
    }

    fun setSelectedLongTermDate(date: LocalDate) {
        _state.update { it.copy(selectedLongTermDate = date) }
    }
    fun setSelectedGraphDate(date: LocalDate) {
        _state.update { it.copy(selectedGraphDate = date) }
    }

    fun showLongTermDatePicker(show: Boolean) {
        _state.update { it.copy(showLongTermDatePicker = show) }
    }

    fun selectElement(element: ElementCodelistItem) {
        _state.update { it.copy(selectedElement = element) }
    }
    fun fetchLongTermStats(stationId: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val date =
                    _state.value.selectedLongTermDate?.toString() ?: java.time.LocalDate.now()
                        .toString()

                _state.update { it.copy(selectedLongTermDate = LocalDate.parse(date)) }

                // Fetch data using the first date
                val dailyStats = repository.getStatsDayLongTerm(stationId, date)

                // Fetch data using the second date

                // Update the state with the fetched data
                _state.update {
                    it.copy(
                        dailyStats = dailyStats,
                        isLoading = false
                    )
                }


            } catch (t: Throwable) {
                _state.update { it.copy(error = t.message, isLoading = false) }
            }
            
        }
        }
    fun fetchConcreteDayData(stationId: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val date = _state.value.selectedConcreteDayDate?.toString() ?: java.time.LocalDate.now().minusYears(1)
                    .toString()

                _state.update { it.copy(selectedLongTermDate = LocalDate.parse(date)) }

                val concreteDayMeasurements =
                    repository.getStatsDay(stationId, date)

                _state.update {
                    it.copy(
                        statsDay = concreteDayMeasurements,
                        isLoading = false
                    )
                }

            } catch (t: Throwable) {
                _state.update { it.copy(error = t.message, isLoading = false) }
            }
        }
    }

    // In HistoryContentViewModel.kt

    fun fetchDailyMeasurements(stationId: String, element: String, date: LocalDate) {
        viewModelScope.launch {
            // Set loading state
            _state.update { it.copy(isLoading = true, error = null)}

            try {
                // Fetch daily measurements from the repository
                val dailyMeasurements = repository.getMeasurementsDayAndMonth(stationId, date.toString(), element)

                // Update state with the fetched data
                _state.update {
                    it.copy(
                        dailyAndMonthlyMeasurements = dailyMeasurements,
                        isLoading = false,
                        error = null
                    )
                }

            } catch (e: Exception) {
                // Handle errors and update state
                _state.update {
                    it.copy(
                        error = e.message ?: "Failed to fetch daily measurements",
                        isLoading = false
                    )
                }

            }
        }
    }

    fun fetchMonthlyMeasurements(stationId: String, element: String, date: LocalDate) {
        viewModelScope.launch {
            // Set loading state
            _state.update { it.copy(isLoading = true, error = null) }
            // Set loading state

            try {
                // Fetch monthly measurements from the repository
                val monthlyMeasurements = repository.getMeasurementsMonth(stationId, date.toString(),element)



                // Update state with the fetched data
                _state.update {
                    it.copy(
                        monthlyMeasurements = monthlyMeasurements,
                        isLoading = false,
                        error = null
                    )
                }

            } catch (e: Exception) {
                // Handle errors and update state

                _state.update {
                    it.copy(
                        error = e.message ?: "Failed to fetch monthly measurements",
                        isLoading = false
                    )}

            }
        }
    }}