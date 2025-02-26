package cz.cvut.weatherforge.features.stations.data.api

import cz.cvut.weatherforge.features.stations.data.Station

interface StationRemoteDataSource {
    suspend fun getStations(): List<Station>

}