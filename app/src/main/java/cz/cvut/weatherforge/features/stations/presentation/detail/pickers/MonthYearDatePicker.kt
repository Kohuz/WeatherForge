package cz.cvut.weatherforge.features.stations.presentation.detail.pickers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.ui.res.stringResource
import cz.cvut.weatherforge.R

@Composable
fun MonthYearDatePicker(
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    // State for year and month
    var selectedYear by remember { mutableStateOf(LocalDate.now().year) }
    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_month_and_year)) }, // Localized title
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Year Picker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.year)) // Localized label
                    Spacer(modifier = Modifier.width(8.dp))
                    YearPicker(
                        selectedYear = selectedYear,
                        onYearSelected = { year -> selectedYear = year },
                        minimumYear = 1950, // Set a minimum year (can be adjusted)
                        maximumYear = LocalDate.now().year, // Set the maximum year to the current year
                        startFromCurrentYear = true // Start from the current year in descending order
                    )
                }

                // Month Picker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.month)) // Localized label
                    Spacer(modifier = Modifier.width(8.dp))
                    DropdownMenuForMonths(
                        selectedMonth = selectedMonth,
                        onMonthSelected = { month -> selectedMonth = month }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedDate = LocalDate.of(selectedYear, selectedMonth, 1) // Default to the first day of the month
                    onDateSelected(selectedDate)
                    onDismiss()
                },
                shape = MaterialTheme.shapes.medium, // Rounded corners
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(stringResource(R.string.ok)) // Localized button text
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                shape = MaterialTheme.shapes.medium, // Rounded corners
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(stringResource(R.string.cancel)) // Localized button text
            }
        }
    )
}





