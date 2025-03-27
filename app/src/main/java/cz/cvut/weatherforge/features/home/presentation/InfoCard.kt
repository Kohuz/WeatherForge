import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppTypography
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPagerIndicator
import cz.cvut.weatherforge.features.stations.data.model.Station

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SwipeableInfoCard(
    infoCards: List<InfoCardData>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState()

    Column(modifier = modifier) {
        HorizontalPager(
            count = infoCards.size,
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            InfoCard(
                title = infoCards[page].title,
                items = infoCards[page].items
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp),
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun InfoCard(
    title: String,
    items: List<Pair<String, String>>,
    isClickable: Boolean = false,
    onClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            items.forEach { (label, value) ->
                WeatherInfoRow(
                    label = label,
                    value = value,
                    isClickable = isClickable,
                    onClick = { onClick(label) }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun NearbyStationInfoCard(
    title: String,
    items: List<Pair<Station,Double>>,
    onClick: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            items.forEach { (station, value) ->
                WeatherInfoRow(
                    label = station.location,
                    value = "${String.format("%.1f", value)} km",
                    isClickable = true,
                    onClick = { onClick(station.stationId) }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun WeatherInfoRow(
    label: String,
    value: String,
    isClickable: Boolean = false, // Add this parameter
    onClick: () -> Unit = {} // Add this parameter (default empty lambda)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isClickable) {
                    Modifier.clickable { onClick() } // Apply clickable modifier conditionally
                } else {
                    Modifier
                }
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = AppTypography.bodyMedium)
        Text(text = value, style = AppTypography.bodyMedium)
    }
}

data class InfoCardData(
    val title: String,
    val items: List<Pair<String, String>>
)