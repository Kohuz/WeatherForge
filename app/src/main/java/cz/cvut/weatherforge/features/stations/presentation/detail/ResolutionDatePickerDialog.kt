import androidx.compose.runtime.Composable
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.DailyDatePicker
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.DayMonthlyDatePicker
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.MonthDatePicker
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.MonthYearDatePicker
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
        "Měsíc a rok" -> {
            MonthYearDatePicker(onDismiss, onDateSelected)
        }
        "Den a měsíc" ->{
            DayMonthlyDatePicker(minimumDate, onDismiss, onDateSelected)
        }
        "Měsíčně" ->{
            MonthDatePicker(onDismiss, onDateSelected)
        }
        "Ročně" -> {
            YearlyDatePicker(minimumDate, onDismiss, onDateSelected)
        }
    }
}