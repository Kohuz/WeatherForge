package cz.cvut.weatherforge.features.stations.presentation.detail.pickers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthlyDatePicker(
    minimumDate: LocalDate?,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val currentYear = LocalDate.now().year // Use the current year
    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var selectedDay by remember { mutableStateOf(LocalDate.now().dayOfMonth) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Vyberte den a měsíc") }, // Localized title
        text = {
            Column {
                // Month Picker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Měsíc:") // Localized label
                    Spacer(modifier = Modifier.width(8.dp))
                    DropdownMenuForMonths(
                        selectedMonth = selectedMonth,
                        onMonthSelected = { month -> selectedMonth = month }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Day Picker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Den:") // Localized label
                    Spacer(modifier = Modifier.width(8.dp))
                    DayPicker(
                        selectedDay = selectedDay,
                        onDaySelected = { day -> selectedDay = day },
                        maxDaysInMonth = YearMonth.of(currentYear, selectedMonth).lengthOfMonth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedDate = LocalDate.of(currentYear, selectedMonth, selectedDay)
                    onDateSelected(selectedDate)
                    onDismiss()
                }
            ) {
                Text("OK") // Localized button text
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Zrušit") // Localized button text
            }
        }
    )
}

@Composable
fun DropdownMenuForMonths(
    selectedMonth: Int,
    onMonthSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Mapping of Month enum to Czech month names
    val czechMonths = mapOf(
        Month.JANUARY to "Leden",
        Month.FEBRUARY to "Únor",
        Month.MARCH to "Březen",
        Month.APRIL to "Duben",
        Month.MAY to "Květen",
        Month.JUNE to "Červen",
        Month.JULY to "Červenec",
        Month.AUGUST to "Srpen",
        Month.SEPTEMBER to "Září",
        Month.OCTOBER to "Říjen",
        Month.NOVEMBER to "Listopad",
        Month.DECEMBER to "Prosinec"
    )

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        Button(onClick = { expanded = true }) {
            Text(czechMonths[Month.of(selectedMonth)] ?: "Unknown") // Display Czech month name
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Month.entries.forEach { month ->
                DropdownMenuItem(
                    text = { Text(czechMonths[month] ?: "Unknown") }, // Display Czech month name
                    onClick = {
                        onMonthSelected(month.value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DayPicker(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    maxDaysInMonth: Int
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        Button(onClick = { expanded = true }) {
            Text("$selectedDay")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            (1..maxDaysInMonth).forEach { day ->
                DropdownMenuItem(
                    text = { Text("$day") },
                    onClick = {
                        onDaySelected(day)
                        expanded = false
                    }
                )
            }
        }
    }
}