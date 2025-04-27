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

class DayContentViewModel(
    private val repository: MeasurementRepository
) : ViewModel() {

    data class DayContentState(
        val selectedConcreteDayDate: LocalDate? = null,
        val selectedLongTermDate: LocalDate? = null,

        val dropdownExpanded: Boolean = false,

        val showConcreteDayDatePicker: Boolean = false,
        val showLongTermDatePicker: Boolean = false,


        val dailyStats: ValueStatsResult? = null,
        val statsDay: MeasurementDailyResult? = null,
        val isLoading: Boolean = false,

        val error: String? = null
    )

    // State for the HistoryContent
    private val _state = MutableStateFlow(DayContentState())
    val historyContentState get() = _state.asStateFlow()

    fun setSelectedConcreteDayDate(date: LocalDate) {
        _state.update { it.copy(selectedConcreteDayDate = date) }
    }

    fun showConcreteDatePicker(show: Boolean) {
        _state.update { it.copy(showConcreteDayDatePicker = show) }
    }

    fun setSelectedLongTermDate(date: LocalDate) {
        _state.update { it.copy(selectedLongTermDate = date) }
    }

    fun fetchLongTermStats(stationId: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val date =
                    _state.value.selectedLongTermDate?.toString() ?: java.time.LocalDate.now()
                        .toString()

                _state.update { it.copy(selectedLongTermDate = LocalDate.parse(date)) }

                // Fetch longterm data using the selected date
                val dailyStats = repository.getStatsDayLongTerm(stationId, date)


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
                val date =
                    _state.value.selectedConcreteDayDate?.toString() ?: java.time.LocalDate.now()
                        .minusYears(1)
                        .toString()

                _state.update { it.copy(selectedConcreteDayDate  = LocalDate.parse(date)) }

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

}

