package com.kozubek.livesport.features.sportEntries.data.db

import com.kozubek.livesport.features.sportEntries.data.StationLocalDataSource

import cz.cvut.weatherforge.features.stations.data.db.DbStation
import cz.cvut.weatherforge.features.stations.data.model.Station

class StationRoomDataSource(private val stationDao: StationDao) : StationLocalDataSource {

    override suspend fun getStations(): List<Station> {
        return stationDao.getSportEntries().map { it.toStation() }
    }

    override suspend fun getStation(stationId: String): Station? {
        return stationDao.getSportEntry(stationId)?.toStation()
    }

    override suspend fun insert(stations: List<Station>) {
        stationDao.insert(stations.map { it.toDb() })
    }

    override suspend fun deleteAll() {
        stationDao.deleteAll()
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