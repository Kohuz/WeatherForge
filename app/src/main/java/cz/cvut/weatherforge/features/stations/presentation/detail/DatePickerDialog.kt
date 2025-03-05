import androidx.compose.runtime.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.window.DialogProperties
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    resolution: String, // "Denně", "Měsíčně", "Ročně"
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val initialDateMillis = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)
    val selectedDate = datePickerState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    selectedDate?.let { date ->
                        // Adjust the selected date based on the resolution
                        val adjustedDate = when (resolution) {
                            "Měsíčně" -> date.withDayOfMonth(1) // Set to the first day of the month
                            "Ročně" -> date.withDayOfMonth(1).withMonth(1) // Set to the first day of the year
                            else -> date // Daily resolution, no adjustment needed
                        }
                        onDateSelected(adjustedDate)
                    }
                    onDismiss()
                }
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
        DatePicker(state = datePickerState)
    }
}