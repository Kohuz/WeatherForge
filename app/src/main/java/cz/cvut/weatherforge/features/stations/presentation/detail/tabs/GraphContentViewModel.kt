package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.weatherforge.features.measurements.data.MeasurementRepository
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementMonthly
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementYearly
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class GraphContentViewModel(
    private val measurementRepository: MeasurementRepository
) : ViewModel() {

    private val _graphContentStateStream = MutableStateFlow(GraphContentState())
    val graphContentStateStream get() = _graphContentStateStream.asStateFlow()

    data class GraphContentState(
        val selectedResolutionIndex: Int = 0,
        val expanded: Boolean = false,
        val selectedElement: ElementCodelistItem? = null,
        val fromDate: LocalDate? = null,
        val toDate: LocalDate? = null,
        val showDateRangePicker: Boolean = false,
    )

    fun selectResolution(resolutionIndex: Int) {
        _graphContentStateStream.update { it.copy(selectedResolutionIndex = resolutionIndex) }
    }

    fun toggleDropdown(expanded: Boolean) {
        _graphContentStateStream.update { it.copy(expanded = expanded) }
    }

    fun selectElement(element: ElementCodelistItem) {
        _graphContentStateStream.update { it.copy(selectedElement = element) }
    }

    fun showDateRangePicker(show: Boolean) {
        _graphContentStateStream.update { it.copy(showDateRangePicker = show) }
    }

//    fun showFromDatePicker(show: Boolean) {
//        _graphContentStateStream.update { it.copy(showFromDatePicker = show) }
//    }
//
//    fun showToDatePicker(show: Boolean) {
//        _graphContentStateStream.update { it.copy(showToDatePicker = show) }
//    }

    fun setFromDate(date: LocalDate) {
        _graphContentStateStream.update { it.copy(fromDate = date) }
    }

    fun setToDate(date: LocalDate) {
        _graphContentStateStream.update { it.copy(toDate = date) }
    }
}