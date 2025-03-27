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
        val selectedGraphDate: LocalDate? = null,
        val selectedResolutionIndex: Int = 0,
        val dropdownExpanded: Boolean = false,
        val selectedElement: ElementCodelistItem? = null,
        val showGraphDatePicker: Boolean = false,
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


    fun showGraphDatePicker(show: Boolean) {
        _state.update { it.copy(showGraphDatePicker = show) }
    }

    fun setSelectedGraphDate(date: LocalDate) {
        _state.update { it.copy(selectedGraphDate = date) }
    }



    fun selectElement(element: ElementCodelistItem) {
        _state.update { it.copy(selectedElement = element) }
    }



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