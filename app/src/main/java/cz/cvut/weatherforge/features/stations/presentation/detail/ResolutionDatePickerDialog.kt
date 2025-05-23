import androidx.compose.runtime.Composable
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.DailyDatePicker
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.DayMonthlyDatePicker
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.MonthDatePicker
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.MonthYearDatePicker
import cz.cvut.weatherforge.features.stations.presentation.detail.pickers.YearlyDatePicker
import java.time.LocalDate

@Composable
fun ResolutionDatePickerDialog(
    minimumDate: LocalDate? = null,
    resolution: String,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    dateToShow: LocalDate? = null
) {
    when (resolution) {
        "Denně" -> {
            DailyDatePicker(minimumDate, onDismiss, onDateSelected, dateToShow)
        }
        "Měsíc a rok" -> {
            MonthYearDatePicker(onDismiss, onDateSelected)
        }
        "Den a měsíc" ->{
            DayMonthlyDatePicker(onDismiss, onDateSelected)
        }
        "Měsíčně" ->{
            MonthDatePicker(onDismiss, onDateSelected,minimumDate)
        }
        "Ročně" -> {
            YearlyDatePicker(minimumDate, onDismiss, onDateSelected )
        }
    }
}