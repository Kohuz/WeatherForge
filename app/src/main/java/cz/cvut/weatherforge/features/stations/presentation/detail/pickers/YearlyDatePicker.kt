package cz.cvut.weatherforge.features.stations.presentation.detail.pickers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun YearlyDatePicker(
    minimumDate: LocalDate?,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    var selectedYear by remember { mutableStateOf(minimumDate?.year ?: LocalDate.now().year) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val selectedDate = LocalDate.of(selectedYear, 1, 1)
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
        title = { Text("Select Year") },
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

                // Year List
                val years = (1900..LocalDate.now().year).toList()
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(years.size) { index ->
                        val year = years[index]
                        val isSelected = year == selectedYear
                        OutlinedButton(
                            onClick = { selectedYear = year },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                        ) {
                            Text(text = year.toString())
                        }
                    }
                }
            }
        }
    )
}