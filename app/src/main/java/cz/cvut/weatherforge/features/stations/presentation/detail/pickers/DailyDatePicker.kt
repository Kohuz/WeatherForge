package cz.cvut.weatherforge.features.stations.presentation.detail.pickers

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyDatePicker(
    minimumDate: LocalDate?,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    dateToShow: LocalDate? = null
) {
    // Calculate the initial selected date in milliseconds
    val initialSelectedDateMillis = dateToShow
        ?.withDayOfMonth(1) // Start of the month
        ?.atStartOfDay(ZoneId.systemDefault())
        ?.toInstant()
        ?.toEpochMilli()

    // Define the year range (e.g., from the minimum date's year to the current year)
    val yearRange = IntRange(minimumDate?.year ?: 1900, LocalDate.now().year)

    // Set the locale to Czech
    val locale = Locale("cs", "CZ")

    // Calculate the initial displayed month in milliseconds (default to the current month)
    val initialDisplayedMonthMillis = dateToShow
        ?.withDayOfMonth(1) // Start of the month
        ?.atStartOfDay(ZoneId.systemDefault())
        ?.toInstant()
        ?.toEpochMilli()

    // Custom SelectableDates to limit the range from minimumDate to today
    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            val today = LocalDate.now()
            return date.isAfter(minimumDate?.minusDays(1)) && !date.isAfter(today) // Include today
        }

        override fun isSelectableYear(year: Int): Boolean {
            return year in yearRange
        }
    }

    // Initialize the DatePickerState
    val datePickerState = DatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        yearRange = yearRange,
        locale = locale,
        initialDisplayedMonthMillis = initialDisplayedMonthMillis,
        initialDisplayMode = DisplayMode.Picker, // Use the standard calendar view
        selectableDates = selectableDates // Apply the custom date range limitation
    )

    // DatePickerDialog
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    onDateSelected(selectedDate)
                }
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}