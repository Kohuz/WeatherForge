package com.kozubek.livesport.features.sportEntries.data.db

import com.kozubek.livesport.features.sportEntries.data.StationLocalDataSource

import cz.cvut.weatherforge.features.stations.data.db.DbStation
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station

class StationRoomDataSource(private val stationDao: StationDao, private val elementCodelistDao: ElementCodelistDao) : StationLocalDataSource {

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

    override suspend fun insertCodelist(codelistItems: List<ElementCodelistItem>) {
        elementCodelistDao.insertElements(codelistItems)
    }

    override suspend fun getElements(): List<ElementCodelistItem> {
        return elementCodelistDao.getElements()
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
            stationElements = emptyList(), // Add station elements if available
            stationLatestMeasurements = emptyList() // Add latest measurements if available
        )
    }

    private fun Station.toDb(): DbStation {
        return DbStation(
            id = this.stationId, // Assuming 'stationId' is the primary key
            stationId = this.stationId,
            code = this.code,
            startDate = this.startDate,
            endDate = this.endDate,
            location = this.location,
            longitude = this.longitude,
            latitude = this.latitude,
            elevation = this.elevation
        )
    }
}