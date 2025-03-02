package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import InfoCard
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreenViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.elementAbbreviationToNameUnitPair
import cz.cvut.weatherforge.features.stations.presentation.list.FilterChangeButtons
import cz.cvut.weatherforge.features.stations.presentation.list.ResultCard
import cz.cvut.weatherforge.features.stations.presentation.list.ResultHeading

@Composable
fun OverviewContent(station: Station, viewModel: DetailScreenViewModel) {
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
       InfoCard(
           title = stringResource(R.string.detail_information_about_station),
           items = listOf(
               Pair(stringResource(R.string.detail_location), station.location),
               Pair(stringResource(R.string.detail_elevation), station.elevation.toString()),
               Pair(stringResource(R.string.detail_coordinates), String.format("%.4f, %.4f", station.latitude, station.longitude)
           )),
       )
        InfoCard(
            title = stringResource(R.string.detail_current_state),
            items = station.stationLatestMeasurements.mapNotNull { measurement ->
                val elementInfo = elementAbbreviationToNameUnitPair(measurement.element, screenState.elementCodelist)
                if (elementInfo != null) {
                    val valueWithUnit = "${measurement.value} ${elementInfo.unit}"
                    elementInfo.name to valueWithUnit
                } else {
                    null
                }
            }
        )
       
        
    }
}