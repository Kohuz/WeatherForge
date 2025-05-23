package cz.cvut.weatherforge.features.stations.data.db

import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station

class StationRoomDataSource(private val stationDao: StationDao, private val elementCodelistDao: ElementCodelistDao) :
    StationLocalDataSource {

    override suspend fun getStations(): List<Station> {
        return stationDao.getStations().map { it.toStation() }
    }

    override suspend fun getStation(stationId: String): Station? {
        return stationDao.getStation(stationId)?.toStation()
    }

    override suspend fun insertStations(stations: List<Station>) {
        stationDao.insertStations(stations.map { it.toDb() })
    }

    override suspend fun deleteStations() {
        stationDao.deleteStations()
    }

    override suspend fun makeFavorite(stationId: String) {
        return stationDao.makeFavorite(stationId)
    }

    override suspend fun removeFavorite(stationId: String){
        return stationDao.removeFavorite(stationId)
    }

    override suspend fun insertCodelist(codelistItems: List<ElementCodelistItem>) {
        elementCodelistDao.insertElements(codelistItems.map { it.toDb() })
    }

    override suspend fun getElements(): List<ElementCodelistItem> {
        return elementCodelistDao.getElements().map { it.toElementCodelistItem() }
    }

    private fun DbStation.toStation(): Station {
        return Station(
            stationId = this.stationId,
            code = this.code,
            startDate = this.startDate,
            endDate = this.endDate,
            location = this.location,
            longitude = this.longitude,
            latitude = this.latitude,
            elevation = this.elevation,
            stationElements = emptyList(),
            isFavorite = this.isFavorite,
            stationLatestMeasurements = emptyList()
        )
    }

    private fun Station.toDb(): DbStation {
        return DbStation(
            id = this.stationId,
            stationId = this.stationId,
            code = this.code,
            startDate = this.startDate,
            endDate = this.endDate,
            location = this.location,
            longitude = this.longitude,
            latitude = this.latitude,
            elevation = this.elevation,
            isFavorite = false
        )
    }

    private fun DbElementCodelistItem.toElementCodelistItem(): ElementCodelistItem {
        return ElementCodelistItem(
            abbreviation = this.abbreviation,
            unit = this.unit,
            name = this.name
        )
    }

    private fun ElementCodelistItem.toDb(): DbElementCodelistItem {
        return DbElementCodelistItem(
            abbreviation = this.abbreviation,
            name = this.name,
            unit = this.unit
        )
    }
}
