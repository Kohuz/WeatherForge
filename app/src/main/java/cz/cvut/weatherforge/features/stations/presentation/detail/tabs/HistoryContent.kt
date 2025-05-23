package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import ResolutionDatePickerDialog
import android.util.Log
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
import cz.cvut.weatherforge.core.utils.elementAbbreviationToNameUnitPair
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDailyResult
import cz.cvut.weatherforge.features.record.presentatioin.ElementDropdownMenu
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreenViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.DailyChart
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.chart.MonthlyChart
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate

val resolutions = listOf("Den a měsíc", "Měsíčně")

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

    LaunchedEffect(historyContentState.selectedGraphDate, historyContentState.selectedElement, historyContentState.selectedResolutionIndex) {
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


        ResolutionSelector(historyContentViewModel, resolutions, selectedResolution)

        ElementDropdownMenu(
            items = detailState.elementCodelist,
            selectedItem = historyContentState.selectedElement,
            onItemSelected = { element ->
                historyContentViewModel.selectElement(element)
            },
            allowedElements = listOf("TMI", "T", "F", "TMA", "SCE", "SNO", "SRA", "Fmax")
        )

        ChartDateSelector(
            historyContentViewModel,
            historyContentState,
            detailState.station?.startDate?.date?.toJavaLocalDate(),
            resolutions[selectedResolution]
        )

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
    pickerContent: @Composable () -> Unit,
    state: HistoryContentViewModel.HistoryContentState
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
                text = "${stringResource(labelRes)} ${
                    date?.formatForResolution(resolutions[state.selectedResolutionIndex])
                        ?: stringResource(R.string.no_date_selected)
                }".trim()
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
            ResolutionChip(
                resolution = resolution,
                isSelected = index == selectedResolution,
                onSelected = { viewModel.selectResolution(index) },
            )
        }
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
        labelRes = if(resolution == "Den a měsíc") R.string.select_day_month else R.string.select_month,
        date = state.selectedGraphDate?.toJavaLocalDate(),
        showPicker = state.showGraphDatePicker,
        onShowPicker = { viewModel.showGraphDatePicker(true) },
        state = state,
        pickerContent = {
            ResolutionDatePickerDialog(
                minimumDate = minDate,
                resolution = resolution,
                onDismiss = { viewModel.showGraphDatePicker(false) },
                onDateSelected = { date ->
                    viewModel.setSelectedGraphDate(date.toKotlinLocalDate())
                },
                dateToShow = state.selectedGraphDate?.toJavaLocalDate() ?: LocalDate.now().minusYears(1)
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
                "Den a měsíc" -> {
                    when {
                        state.isLoading -> LoadingIndicator()

                        state.dailyAndMonthlyMeasurements != null -> {
                            DailyChart(state.dailyAndMonthlyMeasurements.measurements, history = true)
                        }
                        else -> Text("No daily data available")
                    }
                }
                "Měsíčně" -> {
                    when {
                        state.isLoading -> LoadingIndicator()
                        state.monthlyMeasurements != null -> {
                            MonthlyChart(state.monthlyMeasurements.measurements, history = true)
                        }
                        else -> Text("No monthly data available")
                    }
                }
            }
        }
        else -> {
            Text(stringResource(R.string.select_element_to_show))
        }
    }
}