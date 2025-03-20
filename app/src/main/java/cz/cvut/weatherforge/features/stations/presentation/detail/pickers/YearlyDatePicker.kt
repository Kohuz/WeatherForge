package cz.cvut.weatherforge.features.stations.presentation.detail.pickers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.ui.res.stringResource
import cz.cvut.weatherforge.R

@Composable
fun YearlyDatePicker(
    minimumDate: LocalDate?,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    var selectedYear by remember { mutableStateOf(minimumDate?.year ?: LocalDate.now().year) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_year)) }, // Localized title
        text = {
            Column {
                // Use the YearPicker composable
                YearPicker(
                    selectedYear = selectedYear,
                    onYearSelected = { year -> selectedYear = year },
                    minimumYear = minimumDate?.year ?: 1950,
                    maximumYear = LocalDate.now().year,
                    startFromCurrentYear = true // Start from the current year in descending order
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedDate = LocalDate.of(selectedYear, 1, 1) // Default to the first day of the year
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

@Composable
fun YearPicker(
    selectedYear: Int,
    onYearSelected: (Int) -> Unit,
    minimumYear: Int,
    maximumYear: Int,
    startFromCurrentYear: Boolean = false
) {
    val years = remember {
        if (startFromCurrentYear) {
            (maximumYear downTo minimumYear).toList() // Descending order
        } else {
            (minimumYear..maximumYear).toList() // Ascending order
        }
    }

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        OutlinedButton(
            onClick = { expanded = true },
            shape = MaterialTheme.shapes.medium, // Rounded corners
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("$selectedYear")
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.select_year),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.5f) // Limit dropdown width
        ) {
            years.forEach { year ->
                DropdownMenuItem(
                    onClick = {
                        onYearSelected(year)
                        expanded = false
                    },
                    text = { Text("$year") }
                )
            }
        }
    }
}