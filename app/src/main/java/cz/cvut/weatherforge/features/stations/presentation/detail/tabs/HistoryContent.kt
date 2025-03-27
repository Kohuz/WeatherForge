package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import ResolutionDatePickerDialog
import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDailyResult
import cz.cvut.weatherforge.features.record.presentation.ElementDropdownMenu
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreenViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.elementAbbreviationToNameUnitPair
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.DailyChart
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.MonthlyChart
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate


@Composable
fun HistoryContent(
    stationId: String,
    historyContentViewModel: HistoryContentViewModel,
    detailViewModel: DetailScreenViewModel
) {
    val historyContentState by historyContentViewModel.historyContentState.collectAsStateWithLifecycle()
    val detailState by detailViewModel.screenStateStream.collectAsStateWithLifecycle()
    val resolutions = listOf("Den a měsíc", "Měsíčně")
    val selectedResolution = historyContentState.selectedResolutionIndex

    LaunchedEffect(historyContentState.selectedGraphDate, historyContentState.selectedElement) {
        historyContentState.selectedElement?.let { element ->
            historyContentState.selectedGraphDate?.let { date ->
                when (selectedResolution) {
                    0 -> historyContentViewModel.fetchDailyMeasurements(stationId, element.abbreviation, date)
                    1 -> historyContentViewModel.fetchMonthlyMeasurements(stationId, element.abbreviation, date)
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {


        // Resolution selector
        ResolutionSelector(historyContentViewModel, resolutions, selectedResolution)

        // With this:
        Text(
            text = stringResource(R.string.select_element),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ElementDropdownMenu(
            items = detailState.elementCodelist,
            selectedItem = historyContentState.selectedElement,
            onItemSelected = { element ->
                historyContentViewModel.selectElement(element)
            },
            allowedElements = listOf("TMI", "T", "F", "TMA", "SCE", "SNO", "SRA", "Fmax")
        )

        // Chart date selection
        ChartDateSelector(
            historyContentViewModel,
            historyContentState,
            detailState.station?.startDate?.date?.toJavaLocalDate(),
            resolutions[selectedResolution]
        )

        // Chart display
        ChartDisplay(
            historyContentState,
            selectedResolution,
            resolutions
        )
    }
}



@Composable
private fun DateSelectionButton(
    labelRes: Int,
    date: LocalDate?,
    showPicker: Boolean,
    onShowPicker: () -> Unit,
    pickerContent: @Composable () -> Unit
) {
    OutlinedButton(
        onClick = onShowPicker,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(
                    labelRes,
                    date?.toString() ?: stringResource(R.string.no_date_selected)
                )
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.select_date),
                modifier = Modifier.size(24.dp)
            )
        }
    }

    if (showPicker) {
        pickerContent()
    }
}



@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}



@Composable
private fun ConcreteDayStatsCard(
    statsDay: MeasurementDailyResult,
    elementCodelist: List<ElementCodelistItem>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.daily_stats),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            statsDay.measurements.forEach { measurement ->
                val nameUnitPair = elementAbbreviationToNameUnitPair(measurement.element, elementCodelist)
                MeasurementRow(measurement, nameUnitPair)
            }
        }
    }
}

@Composable
private fun MeasurementRow(
    measurement: MeasurementDaily,
    nameUnitPair: ElementCodelistItem?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        nameUnitPair?.let {
            Text(
                text = it.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${measurement.value} ${it.unit}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ResolutionSelector(
    viewModel: HistoryContentViewModel,
    resolutions: List<String>,
    selectedResolution: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        resolutions.forEachIndexed { index, resolution ->
            ResolutionRadioButton(
                text = resolution,
                selected = index == selectedResolution,
                onClick = { viewModel.selectResolution(index) }
            )
        }
    }
}

@Composable
private fun ResolutionRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // null because we handle click in parent Row
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}



@Composable
private fun ChartDateSelector(
    viewModel: HistoryContentViewModel,
    state: HistoryContentViewModel.HistoryContentState,
    minDate: LocalDate?,
    resolution: String
) {
    DateSelectionButton(
        labelRes = R.string.select_day_month,
        date = state.selectedGraphDate?.toJavaLocalDate(),
        showPicker = state.showGraphDatePicker,
        onShowPicker = { viewModel.showGraphDatePicker(true) },
        pickerContent = {
            ResolutionDatePickerDialog(
                minimumDate = minDate,
                resolution = resolution,
                onDismiss = { viewModel.showGraphDatePicker(false) },
                onDateSelected = { date ->
                    viewModel.setSelectedGraphDate(date.toKotlinLocalDate())
                },
                dateToShow = LocalDate.now().minusYears(1)
            )
        }
    )
}

@Composable
private fun ChartDisplay(
    state: HistoryContentViewModel.HistoryContentState,
    selectedResolution: Int,
    resolutions: List<String>
) {
    when {
        state.selectedElement != null && state.selectedGraphDate != null -> {
            when (resolutions[selectedResolution]) {
                "Denně" -> {
                    when {
                        state.dailyAndMonthlyMeasurements != null ->
                            DailyChart(state.dailyAndMonthlyMeasurements.measurements)
                        else -> LoadingIndicator()
                    }
                }
                "Měsíčně" -> {
                    when {
                        state.monthlyMeasurements != null ->
                            MonthlyChart(state.monthlyMeasurements.measurements)
                        else -> LoadingIndicator()
                    }
                }
            }
        }
    }
}