package cz.cvut.weatherforge.features.stations.data

import android.util.Log
import cz.cvut.weatherforge.features.stations.data.api.StationRemoteDataSource
import cz.cvut.weatherforge.features.stations.data.model.StationResult
import cz.cvut.weatherforge.features.stations.data.model.StationsResult

class StationRepository(
    private val stationRemoteDataSource: StationRemoteDataSource,
) {
    suspend fun getStations(): StationsResult {
            return try {
                val stations = stationRemoteDataSource.getStations()
                StationsResult(stations, isSuccess = true)
            } catch (t: Throwable) {
                Log.v("api", t.toString())
                StationsResult(emptyList(), isSuccess = false)
            }
    }
    suspend fun getStation(stationId: String): StationResult {
        return try {
            val station = stationRemoteDataSource.getStation(stationId)
            StationResult(station, isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            StationResult(null, isSuccess = false)
        }
    }
}