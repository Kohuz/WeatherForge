package cz.cvut.weatherforge.features.stations.data

import android.util.Log
import cz.cvut.weatherforge.features.stations.data.api.StationRemoteDataSource

class StationRepository(
    private val stationRemoteDataSource: StationRemoteDataSource,
) {
    suspend fun getStations(name: String? = null,elevationMin: Number? = null,elevationMax: Number? = null, active: Boolean? = null): StationResult {
            return try {
                val stations = stationRemoteDataSource.getStations(name, elevationMin, elevationMax, active)
                StationResult(stations, isSuccess = true)
            } catch (t: Throwable) {
                Log.v("gg", t.toString())
                StationResult(emptyList(), isSuccess = false)
            }
        }

//        suspend fun getSportEntry(id: String): SportEntry? {
//            return sportEntryLocalDataSource.getSportEntry(id)
//        }
}