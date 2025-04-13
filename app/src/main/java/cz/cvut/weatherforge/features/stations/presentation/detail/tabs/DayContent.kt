package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import ResolutionDatePickerDialog
import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.core.utils.elementAbbreviationToNameUnitPair
import cz.cvut.weatherforge.core.utils.getLocalizedDateString
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDailyResult
import cz.cvut.weatherforge.features.measurements.data.model.ValueStatsResult
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreenViewModel

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

    // Effects for data loading
    LaunchedEffects(dayContentViewModel, stationId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Error display
        historyContentState.error?.let { ErrorMessage(it) }

        // Concrete day date picker
        DateSelectionButton(
            labelRes = R.string.select_date_day_content,
            date = historyContentState.selectedConcreteDayDate?.toJavaLocalDate(),
            showPicker = historyContentState.showConcreteDayDatePicker,
            onShowPicker = { dayContentViewModel.showConcreteDatePicker(true) },
            pickerContent = {
                ResolutionDatePickerDialog(
                    minimumDate = detailState.station?.startDate?.date?.toJavaLocalDate(),
                    resolution = "Denně",
                    onDismiss = { dayContentViewModel.showConcreteDatePicker(false) },
                    onDateSelected = { date ->
                        dayContentViewModel.setSelectedConcreteDayDate(date.toKotlinLocalDate())
                        dayContentViewModel.setSelectedLongTermDate(date.toKotlinLocalDate())
                    }
                )
            },
            resolution = "Denně"
        )

        // Single loading indicator for all content
        if (historyContentState.isLoading) {
            LoadingIndicator()
        } else {
            // Show all content once loading is complete
            historyContentState.statsDay?.let { statsDay ->
                historyContentState.selectedConcreteDayDate?.let { date ->
                    ConcreteDayStatsCard(
                        statsDay = statsDay,
                        elementCodelist = detailState.elementCodelist,
                        date = date.toJavaLocalDate()
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            historyContentState.dailyStats?.let { dailyStats ->
                DailyStatsCard(dailyStats)
            }
        }
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
private fun DateSelectionButton(
    labelRes: Int,
    date: LocalDate?,
    showPicker: Boolean,
    onShowPicker: () -> Unit,
    pickerContent: @Composable () -> Unit,
    resolution: String
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
            if (date != null) {
                Text(
                    text = stringResource(
                        labelRes,
                        date.formatForResolution(resolution)
                    )
                )
            }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Insights,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = stringResource(R.string.weather_statistics),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = stringResource(R.string.historical_averages),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Use a Column to stack the rows vertically
            Column(modifier = Modifier.fillMaxWidth()) {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val columnWidth = maxWidth / 2 - 8.dp // Half width minus padding

                    // First row - Temperature and Precipitation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.width(columnWidth)) {
                            TemperatureStats(dailyStats)
                        }
                        Box(modifier = Modifier.width(columnWidth)) {
                            PrecipitationStats(dailyStats)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val columnWidth = maxWidth / 2 - 8.dp

                    // Second row - Wind and Snow
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.width(columnWidth)) {
                            WindStats(dailyStats)
                        }
                        Box(modifier = Modifier.width(columnWidth)) {
                            SnowStats(dailyStats)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TemperatureStats(dailyStats: ValueStatsResult) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.temperature),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        StatItem(
            label = stringResource(R.string.min_temperature),
            value = dailyStats.valueStats.find { it.element == "TMI" }?.lowest?.toString()
        )
        StatItem(
            label = stringResource(R.string.max_temperature),
            value = dailyStats.valueStats.find { it.element == "TMA" }?.highest?.toString()
        )
        StatItem(
            label = stringResource(R.string.avg_temperature),
            value = dailyStats.valueStats.find { it.element == "T" }?.average?.let { "%.1f".format(it) }
        )
    }
}

@Composable
private fun PrecipitationStats(dailyStats: ValueStatsResult) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.precipitation),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        StatItem(
            label = stringResource(R.string.max_precipitation),
            value = dailyStats.valueStats.find { it.element == "SRA" }?.highest?.toString()
        )
        StatItem(
            label = stringResource(R.string.avg_precipitation),
            value = dailyStats.valueStats.find { it.element == "SRA" }?.average?.let { "%.1f".format(it) }
        )
    }
}

@Composable
private fun WindStats(dailyStats: ValueStatsResult) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.wind),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        StatItem(
            label = stringResource(R.string.max_wind),
            value = dailyStats.valueStats.find { it.element == "Fmax" }?.highest?.toString()
        )
        StatItem(
            label = stringResource(R.string.avg_wind),
            value = dailyStats.valueStats.find { it.element == "F" }?.average?.let { "%.1f".format(it) }
        )
    }
}

@Composable
private fun SnowStats(dailyStats: ValueStatsResult) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.snow),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        StatItem(
            label = stringResource(R.string.max_snow),
            value = dailyStats.valueStats.find { it.element == "SCE" }?.highest?.let { "%.1f".format(it) }
        )
        StatItem(
            label = stringResource(R.string.max_new_snow),
            value = dailyStats.valueStats.find { it.element == "SNO" }?.highest?.let { "%.1f".format(it) }
        )
        StatItem(
            label = stringResource(R.string.avg_snow),
            value = dailyStats.valueStats.find { it.element == "SCE" }?.average?.let { "%.1f".format(it) }
        )
        StatItem(
            label = stringResource(R.string.avg_new_snow),
            value = dailyStats.valueStats.find { it.element == "SNO" }?.average?.let { "%.1f".format(it) }
        )
    }
}

@Composable
private fun StatItem(label: String, value: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = value?.let {
                String.format(label, it)


            } ?: "--",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
@Composable
private fun ConcreteDayStatsCard(
    statsDay: MeasurementDailyResult,
    elementCodelist: List<ElementCodelistItem>,
    date: LocalDate
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${stringResource(R.string.daily_stats)} ${getLocalizedDateString(date)}",
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

