package cz.cvut.weatherforge.features.stations.data

import android.util.Log
import com.kozubek.livesport.features.sportEntries.data.StationLocalDataSource
import cz.cvut.weatherforge.features.stations.data.api.StationRemoteDataSource
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.StationResult
import cz.cvut.weatherforge.features.stations.data.model.StationsResult

class StationRepository(
    private val stationRemoteDataSource: StationRemoteDataSource,
    private val stationLocalDataSource: StationLocalDataSource,
) {

    suspend fun getStations(): StationsResult {
        return try {
            val localStations = stationLocalDataSource.getStations()
            if (localStations.isNotEmpty()) {
                StationsResult(localStations, isSuccess = true)
            } else {
                val remoteStations = stationRemoteDataSource.getStations()
                stationLocalDataSource.insertStations(remoteStations)
                StationsResult(remoteStations, isSuccess = true)
            }
        } catch (t: Throwable) {
            Log.e("StationRepository", "Error fetching stations: ${t.message}")
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

    suspend fun getClosestStation(lat: Float, long: Float): StationResult {
        return try {
            val stations = stationRemoteDataSource.getClosest(lat, long, 1)
            StationResult(stations.first(), isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            StationResult(null, isSuccess = false)
        }
    }

    suspend fun getNearbyStations(lat: Float, long: Float): StationsResult {
        return try {
            val stations = stationRemoteDataSource.getClosest(lat, long, 4)
            StationsResult(stations, isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            StationsResult(emptyList(), isSuccess = false)
        }
    }

    suspend fun getElementsCodelist(): List<ElementCodelistItem> {
        return try {
            val localStations = stationLocalDataSource.getStations()
            if (localStations.isNotEmpty()) {
                StationsResult(localStations, isSuccess = true)
            } else {
                val remoteStations = stationRemoteDataSource.getStations()
                stationLocalDataSource.insertStations(remoteStations)
                StationsResult(remoteStations, isSuccess = true)
            }
        } catch (t: Throwable) {
            Log.e("StationRepository", "Error fetching stations: ${t.message}")
            StationsResult(emptyList(), isSuccess = false)
        }
    }

}