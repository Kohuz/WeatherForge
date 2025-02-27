package cz.cvut.weatherforge.features.stations.data.api

import cz.cvut.weatherforge.features.stations.data.GeoJSONStationCollection
import cz.cvut.weatherforge.features.stations.data.Station

class StationRetrofitDataSource(private val apiDescription: StationApiDescription):
    StationRemoteDataSource {
    override suspend fun getStations(): List<Station> {
        return apiDescription.getStations()
    }

    override suspend fun getGeoStations(): GeoJSONStationCollection {
        return apiDescription.getGeoStations()
    }

}