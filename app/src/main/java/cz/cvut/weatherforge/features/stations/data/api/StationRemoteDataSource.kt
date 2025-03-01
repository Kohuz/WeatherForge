package cz.cvut.weatherforge.features.stations.data.api

import cz.cvut.weatherforge.features.stations.data.model.Station

interface StationRemoteDataSource {
    suspend fun getStations(): List<Station>
    suspend fun getClosest(lat: Float, long: Float, count: Int): List<Station>
    suspend fun getStation(stationId: String): Station?

}