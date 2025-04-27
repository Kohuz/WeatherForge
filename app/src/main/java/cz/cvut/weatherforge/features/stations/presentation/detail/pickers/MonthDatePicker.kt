package cz.cvut.weatherforge.features.stations.presentation.detail.pickers
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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

@Composable
fun MonthDatePicker(
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    minimumDate: LocalDate? = LocalDate.of(1950, 1, 1),
) {
    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Vyberte měsíc") },
        text = {
            Column {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Měsíc:")
                    Spacer(modifier = Modifier.width(8.dp))
                    DropdownMenuForMonths(
                        selectedMonth = selectedMonth,
                        onMonthSelected = { month -> selectedMonth = month }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))


            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedDate = LocalDate.of(LocalDate.now().year, selectedMonth, LocalDate.now().dayOfMonth)
                    onDateSelected(selectedDate)
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Zrušit")
            }
        }
    )
}