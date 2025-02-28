package com.kozubek.livesport.features.sportEntries.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cz.cvut.weatherforge.features.stations.data.db.DbStation

@Dao
interface StationDao {
    @Query("SELECT * FROM station")
    suspend fun getSportEntries(): List<DbStation>

    @Query("SELECT * FROM station WHERE stationId = :stationId")
    suspend fun getSportEntry(stationId: String): DbStation?

    @Insert
    suspend fun insert(stations: List<DbStation>)

    @Query("DELETE FROM station")
    suspend fun deleteAll()

}