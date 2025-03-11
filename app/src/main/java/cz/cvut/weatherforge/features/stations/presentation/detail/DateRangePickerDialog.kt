import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.DailyDatePicker
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.MonthlyDatePicker
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.YearlyDatePicker
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun DatePickerDialog(
    minimumDate: LocalDate?,
    resolution: String,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    when (resolution) {
        "Denně" -> {
            DailyDatePicker(minimumDate, onDismiss, onDateSelected)
        }
        "Měsíčně" -> {
            MonthlyDatePicker(minimumDate, onDismiss, onDateSelected)
        }
        "Ročně" -> {
            YearlyDatePicker(minimumDate, onDismiss, onDateSelected)
        }
    }
}