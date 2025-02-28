package cz.cvut.weatherforge.features.stations.data

import android.util.Log
import cz.cvut.weatherforge.features.stations.data.api.StationRemoteDataSource

class StationRepository(
    private val stationRemoteDataSource: StationRemoteDataSource,
) {
    suspend fun getStations(): StationResult {
            return try {
                val stations = stationRemoteDataSource.getStations()
                StationResult(stations, isSuccess = true)
            } catch (t: Throwable) {
                Log.v("api", t.toString())
                StationResult(emptyList(), isSuccess = false)
            }
    }
}