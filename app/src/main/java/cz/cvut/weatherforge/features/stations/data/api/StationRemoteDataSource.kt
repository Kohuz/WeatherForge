package cz.cvut.weatherforge.features.stations.data.api

import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station

interface StationRemoteDataSource {
    suspend fun getStations(): List<Station>
    suspend fun getClosest(lat: Double, long: Double, count: Int): List<Station>
    suspend fun getStation(stationId: String): Station?
    suspend fun getElementsCodelist(): List<ElementCodelistItem>


}