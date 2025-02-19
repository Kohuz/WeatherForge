package cz.cvut.weatherforge.features.stations.data.api

import cz.cvut.weatherforge.features.stations.data.Station

class StationRetrofitDataSource(private val apiDescription: StationApiDescription):
    StationRemoteDataSource {
    override suspend fun getStations(
        name: String?,
        elevationMin: Number?,
        elevationMax: Number?,
        active: Boolean?
    ): List<Station> {
        return apiDescription.getSportEntries(name,
            elevationMin,
            elevationMax,
            active)
    }

}