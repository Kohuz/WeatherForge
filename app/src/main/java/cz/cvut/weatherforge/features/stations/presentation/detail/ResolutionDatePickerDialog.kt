import androidx.compose.runtime.Composable
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.DailyDatePicker
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.DayMonthlyDatePicker
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.YearlyDatePicker
import java.time.LocalDate

@Composable
fun ResolutionDatePickerDialog(
    minimumDate: LocalDate?,
    resolution: String,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    when (resolution) {
        "Denně" -> {
            DailyDatePicker(minimumDate, onDismiss, onDateSelected)
        }
        "Měsíčně" -> {
            MonthYearPicker(minimumDate, onDismiss, onDateSelected)
        }
        "DenMěsíčně" ->{
            DayMonthlyDatePicker(minimumDate, onDismiss, onDateSelected)
        }
        "Ročně" -> {
            YearlyDatePicker(minimumDate, onDismiss, onDateSelected)
        }
    }
}