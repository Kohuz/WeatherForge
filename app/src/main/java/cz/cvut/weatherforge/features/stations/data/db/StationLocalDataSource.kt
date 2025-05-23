package cz.cvut.weatherforge.features.stations.data.db

import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station

interface StationLocalDataSource {
        suspend fun getStations(): List<Station>

        suspend fun getStation(stationId: String): Station?

        suspend fun insertStations(stations: List<Station>)

        suspend fun deleteStations()

        suspend fun makeFavorite(stationId: String)

        suspend fun removeFavorite(stationId: String)

        suspend fun insertCodelist(codelistItems: List<ElementCodelistItem>)

        suspend fun getElements(): List<ElementCodelistItem>

}