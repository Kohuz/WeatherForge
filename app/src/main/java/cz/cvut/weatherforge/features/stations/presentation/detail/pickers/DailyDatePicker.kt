package cz.cvut.weatherforge.features.stations.presentation.detail.pickers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.cvut.weatherforge.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyDatePicker(
    minimumDate: LocalDate?,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    dateToShow: LocalDate? = null
) {
    val initialSelectedDateMillis = dateToShow
        ?.withDayOfMonth(1)
        ?.atStartOfDay(ZoneId.systemDefault())
        ?.toInstant()
        ?.toEpochMilli()

    val yearRange = IntRange(minimumDate?.year ?: 1900, LocalDate.now().year)

    val locale = Locale("cs", "CZ")

    val initialDisplayedMonthMillis = dateToShow
        ?.withDayOfMonth(1)
        ?.atStartOfDay(ZoneId.systemDefault())
        ?.toInstant()
        ?.toEpochMilli()

    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            val today = LocalDate.now()
            return date.isAfter(minimumDate?.minusDays(1)) && !date.isAfter(today)
        }

        override fun isSelectableYear(year: Int): Boolean {
            return year in yearRange
        }
    }

    val datePickerState = DatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        yearRange = yearRange,
        locale = locale,
        initialDisplayedMonthMillis = initialDisplayedMonthMillis,
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = selectableDates
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        onDateSelected(selectedDate)
                    }
                    onDismiss()
                },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Potvrdit",
                    )
                    Text(stringResource(R.string.ok))
                }
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Zrušit",
                    )
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}