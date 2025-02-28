package com.kozubek.livesport.features.sportEntries.data

import cz.cvut.weatherforge.features.stations.data.model.Station

interface StationLocalDataSource {
        suspend fun getStations(): List<Station>

        suspend fun getStation(stationId: String): Station?

        suspend fun insert(stations: List<Station>)

        suspend fun deleteAll()
}