package cz.cvut.weatherforge.features.stations.presentation.detail.pickers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearDatePicker(
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    // State for year, month, and day
    var selectedYear by remember { mutableStateOf(LocalDate.now().year) }
    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Vyberte den, měsíc a rok") }, // Updated title
        text = {
            Column {
                // Year Picker
                YearPicker(
                    selectedYear = selectedYear,
                    onYearSelected = { year -> selectedYear = year },
                    minimumYear = 1950, // Set a minimum year (can be adjusted)
                    maximumYear = LocalDate.now().year // Set the maximum year to the current year
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedDate = LocalDate.of(selectedYear, selectedMonth, LocalDate.now().dayOfMonth)
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