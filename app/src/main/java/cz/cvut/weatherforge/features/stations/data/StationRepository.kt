package cz.cvut.weatherforge.features.stations.data

import android.util.Log
import cz.cvut.weatherforge.features.stations.data.db.StationLocalDataSource
import cz.cvut.weatherforge.features.stations.data.api.StationRemoteDataSource
import cz.cvut.weatherforge.features.stations.data.model.ElementsCodelistResult
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

    suspend fun getClosestStation(lat: Double, long: Double): StationResult {
        return try {
            val stations = stationRemoteDataSource.getClosest(lat, long, 1)
            StationResult(stations.first(), isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            StationResult(null, isSuccess = false)
        }
    }

    suspend fun getNearbyStations(lat: Double, long: Double): StationsResult {
        return try {
            val stations = stationRemoteDataSource.getClosest(lat, long, 4)
            StationsResult(stations, isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            StationsResult(emptyList(), isSuccess = false)
        }
    }

    suspend fun getElementsCodelist(): ElementsCodelistResult {
        return try {
            val localElements = stationLocalDataSource.getElements()
            if (localElements.isNotEmpty()) {
                ElementsCodelistResult(localElements, isSuccess = true)
            } else {
                val remoteElements = stationRemoteDataSource.getElementsCodelist()
                    .map { element ->
                        element.copy(name = translateElementName(element.abbreviation))
                    }
                stationLocalDataSource.insertCodelist(remoteElements)
                ElementsCodelistResult(remoteElements, isSuccess = true)
            }
        } catch (t: Throwable) {
            Log.e("StationRepository", "Error fetching stations: ${t.message}")
            ElementsCodelistResult(emptyList(), isSuccess = false)
        }
    }

    private fun translateElementName(originalName: String): String {
        return when (originalName) {
            "T" -> "Průměrná teplota"
            "TMI" -> "Minimální teplota"
            "TMA" -> "Maximální teplota"
            "F" -> "Průměrná rychlost větru"
            "Fmax" -> "Nejvyšší rychlost větru"
            "SRA" -> "Množství srážek"
            "SNO" -> "Nový sníh"
            "SCE" -> "Výška sněhu"
            else -> originalName
        }
    }

    suspend fun makeFavorite(stationId: String) {
        stationLocalDataSource.makeFavorite(stationId)
    }

    suspend fun removeFavorite(stationId: String) {
        stationLocalDataSource.removeFavorite(stationId)
    }

}