package cz.cvut.weatherforge.features.stations.presentation.detail.pickers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthlyDatePicker(
    minimumDate: LocalDate?,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    var selectedYear by remember { mutableStateOf(minimumDate?.year ?: LocalDate.now().year) }
    var selectedMonth by remember { mutableStateOf(minimumDate?.monthValue ?: LocalDate.now().monthValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val selectedDate = LocalDate.of(selectedYear, selectedMonth, 1)
                onDateSelected(selectedDate)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Select Month and Year") },
        text = {
            Column {
                // Year Picker
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Year: ")
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { selectedYear-- }) {
                        Text("<")
                    }
                    Text(text = selectedYear.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                    Button(onClick = { selectedYear++ }) {
                        Text(">")
                    }
                }

                // Month Picker
                val months = Month.values().toList()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(months.size) { index ->
                        val month = months[index]
                        val isSelected = month.value == selectedMonth
                        OutlinedButton(
                            onClick = { selectedMonth = month.value },
                            modifier = Modifier.padding(4.dp),
                        ) {
                            Text(text = month.getDisplayName(TextStyle.FULL, Locale.getDefault()))
                        }
                    }
                }
            }
        }
    )
}