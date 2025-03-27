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
import cz.cvut.weatherforge.features.measurements.data.model.ValueStatsResult
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
fun DayContent(
    stationId: String,
    dayContentViewModel: DayContentViewModel,
    detailViewModel: DetailScreenViewModel
) {
    val historyContentState by dayContentViewModel.historyContentState.collectAsStateWithLifecycle()
    val detailState by detailViewModel.screenStateStream.collectAsStateWithLifecycle()
    val resolutions = listOf("Den a měsíc", "Měsíčně")

    // Effects for data loading
    LaunchedEffects(dayContentViewModel, stationId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Date selection section
        DateSelectionSection(dayContentViewModel, historyContentState, detailState)

        // Error display
        historyContentState.error?.let { ErrorMessage(it) }

        // Data display
        when {
            historyContentState.isLoading -> LoadingIndicator()
            historyContentState.dailyStats != null -> DailyStatsCard(historyContentState.dailyStats!!)
            else -> LoadingIndicator()
        }

        // Concrete day date picker
        DateSelectionButton(
            labelRes = R.string.select_date,
            date = historyContentState.selectedConcreteDayDate?.toJavaLocalDate(),
            showPicker = historyContentState.showConcreteDayDatePicker,
            onShowPicker = { dayContentViewModel.showConcreteDatePicker(true) },
            pickerContent = {
                ResolutionDatePickerDialog(
                    minimumDate = detailState.station?.startDate?.date?.toJavaLocalDate(),
                    resolution = "Denně",
                    onDismiss = { dayContentViewModel.showConcreteDatePicker(false) },
                    onDateSelected = { date -> dayContentViewModel.setSelectedConcreteDayDate(date.toKotlinLocalDate()) }
                )
            }
        )

        // Concrete day data
        historyContentState.statsDay?.let { ConcreteDayStatsCard(it, detailState.elementCodelist) }



    }
}

@Composable
private fun LaunchedEffects(
    viewModel: DayContentViewModel,
    stationId: String,
) {
    val state by viewModel.historyContentState.collectAsStateWithLifecycle()

    LaunchedEffect(state.selectedConcreteDayDate) {
        viewModel.fetchConcreteDayData(stationId)
    }

    LaunchedEffect(state.selectedLongTermDate) {
        viewModel.fetchLongTermStats(stationId)
    }

}

@Composable
private fun DateSelectionSection(
    viewModel: DayContentViewModel,
    state: DayContentViewModel.DayContentState,
    detailState: DetailScreenViewModel.DetailScreenState
) {
    // Long term date picker
    DateSelectionButton(
        labelRes = R.string.select_day_month,
        date = state.selectedLongTermDate?.toJavaLocalDate(),
        showPicker = state.showLongTermDatePicker,
        onShowPicker = { viewModel.showLongTermDatePicker(true) },
        pickerContent = {
            ResolutionDatePickerDialog(
                minimumDate = state.selectedLongTermDate?.toJavaLocalDate(),
                resolution = "Den a měsíc",
                onDismiss = { viewModel.showLongTermDatePicker(false) },
                onDateSelected = { date -> viewModel.setSelectedLongTermDate(date.toKotlinLocalDate()) }
            )
        }
    )

    Spacer(modifier = Modifier.height(16.dp))


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
private fun ErrorMessage(error: String) {
    Text(
        text = stringResource(R.string.error_message, error),
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(vertical = 8.dp)
    )
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
private fun DailyStatsCard(dailyStats: ValueStatsResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.weather_statistics),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {

                TemperatureStats(dailyStats)
                PrecipitationStats(dailyStats)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {

                WindStats(dailyStats)
                SnowStats(dailyStats)
            }
        }
    }
}

@Composable
private fun TemperatureStats(dailyStats: ValueStatsResult) {
    Column {
        Text(
            text = stringResource(R.string.temperature),
            style = MaterialTheme.typography.titleMedium
        )
        Text(stringResource(R.string.min_temperature, dailyStats.valueStats.find { it.element == "TMI" }?.lowest ?: "--"))
        Text(stringResource(R.string.max_temperature, dailyStats.valueStats.find { it.element == "TMA" }?.highest ?: "--"))
        Text(stringResource(R.string.avg_temperature, dailyStats.valueStats.find { it.element == "T" }?.average?.let { "%.1f".format(it) } ?: "--"))
    }
}

@Composable
private fun PrecipitationStats(dailyStats: ValueStatsResult) {
    Column {
        Text(
            text = stringResource(R.string.precipitation),
            style = MaterialTheme.typography.titleMedium
        )
        Text(stringResource(R.string.max_precipitation, dailyStats.valueStats.find { it.element == "SVH" }?.highest ?: "--"))
        Text(stringResource(R.string.avg_precipitation, dailyStats.valueStats.find { it.element == "SVH" }?.average?.let { "%.1f".format(it) } ?: "--"))
    }
}

@Composable
private fun WindStats(dailyStats: ValueStatsResult) {
    Column {
        Text(
            text = stringResource(R.string.wind),
            style = MaterialTheme.typography.titleMedium
        )

        // Maximum wind speed
        Text(
            text = stringResource(
                R.string.max_wind,
                dailyStats.valueStats.find { it.element == "FMAX" }?.highest ?: "--"
            )
        )

        // Average wind speed
        Text(
            text = stringResource(
                R.string.avg_wind,
                dailyStats.valueStats.find { it.element == "F" }?.average?.let {
                    "%.1f".format(it)
                } ?: "--"
            )
        )
    }
}

@Composable
private fun SnowStats(dailyStats: ValueStatsResult) {
    Column {
        Text(
            text = stringResource(R.string.snow),
            style = MaterialTheme.typography.titleMedium
        )

        // Maximum snow depth
        Text(
            text = stringResource(
                R.string.max_snow,
                dailyStats.valueStats.find { it.element == "SCE" }?.highest ?: "--"
            )
        )

        // Maximum new snow
        Text(
            text = stringResource(
                R.string.max_new_snow,
                dailyStats.valueStats.find { it.element == "SNO" }?.highest?.let {
                    "%.1f".format(it)
                } ?: "--"
            )
        )

        // Average snow depth
        Text(
            text = stringResource(
                R.string.avg_snow,
                dailyStats.valueStats.find { it.element == "SCE" }?.average?.let {
                    "%.1f".format(it)
                } ?: "--"
            )
        )

        // Average new snow
        Text(
            text = stringResource(
                R.string.avg_new_snow,
                dailyStats.valueStats.find { it.element == "SNO" }?.average?.let {
                    "%.1f".format(it)
                } ?: "--"
            )
        )
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

