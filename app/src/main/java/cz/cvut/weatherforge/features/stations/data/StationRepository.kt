package cz.cvut.weatherforge.features.stations.data

import cz.cvut.weatherforge.features.stations.data.api.StationRemoteDataSource

class StationRepository(
    private val stationRemoteDataSource: StationRemoteDataSource,
) {
    suspend fun getStations(name: String? = null,elevationMin: Number? = null,elevationMax: Number? = null, active: Boolean? = null): StationResult {
            return try {
                val sportEntries = stationRemoteDataSource.getStations(name, elevationMin, elevationMax, active)
                StationResult(sportEntries, isSuccess = true)
            } catch (t: Throwable) {
                StationResult(emptyList(), isSuccess = false)
            }
        }

//        suspend fun getSportEntry(id: String): SportEntry? {
//            return sportEntryLocalDataSource.getSportEntry(id)
//        }
}