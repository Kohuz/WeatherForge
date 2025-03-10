package cz.cvut.weatherforge.features.stations.presentation.detail.tabs

import InfoCard
import InfoCardData
import SwipeableInfoCard
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.data.model.isActive
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreenViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.elementAbbreviationToNameUnitPair

@Composable
fun OverviewContent(station: Station, viewModel: DetailScreenViewModel) {
    val screenState by viewModel.screenStateStream.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
       InfoCard(
           title = stringResource(R.string.detail_information_about_station),
           items = listOf(
               Pair(stringResource(R.string.detail_location), station.location),
               Pair(stringResource(R.string.detail_start_of_measurement), station.startDate.toString()),
               Pair(stringResource(R.string.detail_elevation), station.elevation.toString()),
               Pair(stringResource(R.string.detail_coordinates), String.format("%.4f, %.4f", station.latitude, station.longitude)),
               Pair(stringResource(R.string.detail_code), station.code),
               if (!station.isActive()) {
                   Pair(stringResource(R.string.detail_end_of_measurement), station.endDate.toString())
               } else {
                   Pair(stringResource(R.string.detail_active), "")
               }
           )
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

        if (screenState.allTimeRecords.isNotEmpty()) {
            val allTimeRecordData = InfoCardData(
                title = stringResource(R.string.records),
                items = screenState.allTimeRecords.mapNotNull { record ->
                    if(record.element == "TMA" ||
                        record.element == "Fmax" ||
                        record.element == "SVH" ||
                        record.element == "SNO" ||
                        record.element == "SCE") {
                        val elementInfo = elementAbbreviationToNameUnitPair(
                            record.element,
                            screenState.elementCodelist
                        )
                        if (elementInfo != null) {
                            val valueWithUnit =
                                "${record.highest?.value} ${elementInfo.unit}\n ${record.highest?.recordDate.toString()}"
                            elementInfo.name to valueWithUnit
                        } else {
                            null
                        }
                    }
                    else if(record.element == "TMI") {
                        val elementInfo = elementAbbreviationToNameUnitPair(
                            record.element,
                            screenState.elementCodelist
                        )
                        if (elementInfo != null) {
                            val valueWithUnit =
                                "${record.lowest?.value} ${elementInfo.unit}\n ${record.lowest?.recordDate.toString()} "
                            elementInfo.name to valueWithUnit
                        } else {
                            null
                        }
                    }
                    else {
                        val elementInfo = elementAbbreviationToNameUnitPair(
                            record.element,
                            screenState.elementCodelist
                        )
                        if (elementInfo != null) {
                            val valueWithUnit =
                                "${String.format("%.2f", record.average)} ${elementInfo.unit}"
                            elementInfo.name to valueWithUnit
                        } else {
                            null
                        }
                    }

                }
            )
            SwipeableInfoCard(listOf(allTimeRecordData))
        }
       
        
    }
}