package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.weatherforge.features.measurements.data.MeasurementRepository
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDailyResult
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementMonthlyResult
import cz.cvut.weatherforge.features.measurements.data.model.ValueStatsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class HistoryContentViewModel(
    private val repository: MeasurementRepository
) : ViewModel() {

    data class HistoryContentState(
        val selectedDate: LocalDate? = null, // The selected date for fetching data
        val selectedDate2: LocalDate? = null, // The second selected date for fetching data
        val showDatePicker: Boolean = false, // Whether to show the date picker dialog
        val showDatePicker2: Boolean = false, // Whether to show the date picker dialog
        val dailyStats: ValueStatsResult? = null, // Result for daily stats (long term)
        val dailyAndMonthlyMeasurements: MeasurementDailyResult? = null, // Result for daily and monthly measurements
        val monthlyMeasurements: MeasurementMonthlyResult? = null, // Result for monthly measurements
        val statsDay: MeasurementDailyResult? = null, // Result for daily stats
        val isLoading: Boolean = false, // Whether data is being fetched
        val error: String? = null // Error message, if any
    )

    // State for the HistoryContent
    private val _state = MutableStateFlow(HistoryContentState())
    val historyContentState get() = _state.asStateFlow()

    // Function to update the selected date and fetch data
    fun setSelectedDate(date: LocalDate) {
        _state.update { it.copy(selectedDate = date) }
    }

    // Function to show/hide the date picker
    fun showDatePicker(show: Boolean) {
        _state.update { it.copy(showDatePicker = show) }
    }

    fun setSelectedDate2(date: LocalDate) {
        _state.update { it.copy(selectedDate2 = date) }
    }

    fun showDatePicker2(show: Boolean) {
        _state.update { it.copy(showDatePicker2 = show) }
    }
    // Function to fetch all data
    fun fetchAllData(stationId: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val date1 = _state.value.selectedDate?.toString() ?: java.time.LocalDate.now().toString()
                val date2 = _state.value.selectedDate2?.toString() ?:java.time.LocalDate.now().toString()

                // Fetch data using the first date
                val dailyStats = repository.getStatsDayLongTerm(stationId, date2)
                val dailyAndMonthlyMeasurements = repository.getMeasurementsDayAndMonth(stationId, date1)

                // Fetch data using the second date
                val monthlyMeasurements = repository.getMeasurementsMonth(stationId, date2)
                val statsDay = repository.getStatsDay(stationId, date2)

                // Update the state with the fetched data
                _state.update {
                    it.copy(
                        dailyStats = dailyStats,
                        dailyAndMonthlyMeasurements = dailyAndMonthlyMeasurements,
                        monthlyMeasurements = monthlyMeasurements,
                        statsDay = statsDay,
                        isLoading = false
                    )
                }
            } catch (t: Throwable) {
                _state.update { it.copy(error = t.message, isLoading = false) }
            }
        }
    }

}