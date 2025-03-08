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
import kotlinx.datetime.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    resolution: String, // "Denně", "Měsíčně", "Ročně"
    onDismiss: () -> Unit,
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit // Callback for date range selection
) {
    // State for the date range picker
    val dateRangePickerState = rememberDateRangePickerState()

    // State for the selected year and month
    var selectedYear by remember { mutableStateOf(LocalDate.now().year) }
    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }

    // Convert selected dates to LocalDate
    val startDate = dateRangePickerState.selectedStartDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    val endDate = dateRangePickerState.selectedEndDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    // Today's date
    val today = LocalDate.now()

    // Validate date selection
    LaunchedEffect(dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis) {
        if (startDate != null && startDate >= today) {
            // Reset start date if it's today or in the future
            dateRangePickerState.setSelection(null, dateRangePickerState.selectedEndDateMillis)
        }
        if (endDate != null && endDate >= today) {
            // Reset end date if it's today or in the future
            dateRangePickerState.setSelection(dateRangePickerState.selectedStartDateMillis, null)
        }
    }

    // Update the date range picker state when the selected year or month changes
    LaunchedEffect(selectedYear, selectedMonth) {
        val newDate = YearMonth.of(selectedYear, selectedMonth).atDay(1)
//        dateRangePickerState.setSelection(
//            newDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
//            newDate.atEndOfMonth().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
//        )
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (startDate != null && endDate != null) {
                        // Adjust the selected dates based on the resolution
                        val adjustedStartDate = when (resolution) {
                            "Měsíčně" -> startDate.withDayOfMonth(1) // Set to the first day of the month
                            "Ročně" -> startDate.withDayOfMonth(1).withMonth(1) // Set to the first day of the year
                            else -> startDate // Daily resolution, no adjustment needed
                        }

                        val adjustedEndDate = when (resolution) {
                            "Měsíčně" -> endDate.withDayOfMonth(endDate.lengthOfMonth()) // Set to the last day of the month
                            "Ročně" -> endDate.withDayOfMonth(31).withMonth(12) // Set to the last day of the year
                            else -> endDate // Daily resolution, no adjustment needed
                        }

                        // Ensure the start date is before or equal to the end date
                        if (adjustedStartDate <= adjustedEndDate) {
                            onDateRangeSelected(adjustedStartDate, adjustedEndDate)
                        } else {
                            // Handle invalid date range (e.g., show an error message)
                            println("Invalid date range: Start date must be before or equal to end date")
                        }
                    }
                    onDismiss()
                },
                enabled = startDate != null && endDate != null // Enable the button only if both dates are selected
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Year and Month Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Year Dropdown
                var expandedYear by remember { mutableStateOf(false) }
                val years = (1900..LocalDate.now().year).toList()
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { expandedYear = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = selectedYear.toString())
                    }
                    DropdownMenu(
                        expanded = expandedYear,
                        onDismissRequest = { expandedYear = false }
                    ) {
                        years.forEach { year ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedYear = year
                                    expandedYear = false
                                },
                                text = {
                                    Text(text = year.toString())
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Month Dropdown
                var expandedMonth by remember { mutableStateOf(false) }
                val months = (1..12).toList()
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { expandedMonth = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = Month.of(selectedMonth).getDisplayName(TextStyle.FULL, Locale.getDefault()))
                    }
                    DropdownMenu(
                        expanded = expandedMonth,
                        onDismissRequest = { expandedMonth = false }
                    ) {
                        months.forEach { month ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedMonth = month
                                    expandedMonth = false
                                },
                                text = {
                                    Text(text = Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault()))
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date Range Picker
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = "Vyberte rozsah dat",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                headline = {
                    Text(
                        text = "Vyberte počáteční a koncové datum",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    }
}