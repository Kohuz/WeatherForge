package cz.cvut.weatherforge.features.stations.data.api

import cz.cvut.weatherforge.features.stations.data.model.Station

interface StationRemoteDataSource {
    suspend fun getStations(): List<Station>
    suspend fun getStation(stationId: String): Station?
}