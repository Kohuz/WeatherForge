package cz.cvut.weatherforge.features.stations.data.api

import cz.cvut.weatherforge.features.stations.data.GeoJSONStationCollection
import cz.cvut.weatherforge.features.stations.data.Station

interface StationRemoteDataSource {
    suspend fun getStations(): List<Station>
    suspend fun getGeoStations(): GeoJSONStationCollection
}