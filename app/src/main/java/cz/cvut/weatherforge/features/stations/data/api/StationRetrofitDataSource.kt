package cz.cvut.weatherforge.features.stations.data.api

import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station

class StationRetrofitDataSource(private val apiDescription: StationApiDescription):
    StationRemoteDataSource {
    override suspend fun getStations(): List<Station> {
        return apiDescription.getStations()
    }
    override suspend fun getStation(stationId: String): Station? {
        return apiDescription.getStation(stationId)
    }
    override suspend fun getClosest(lat: Float, long: Float, count: Int): List<Station> {
        return apiDescription.getClosest(lat, long, count)

    }

    override suspend fun getElementsCodelist(): List<ElementCodelistItem> {
        return apiDescription.getElementsCodelist()
    }

}