package cz.cvut.weatherforge.features.stations.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cz.cvut.weatherforge.features.stations.data.db.DbStation

@Dao
interface StationDao {
    @Query("SELECT * FROM station")
    suspend fun getStations(): List<DbStation>

    @Query("SELECT * FROM station WHERE stationId = :stationId")
    suspend fun getStation(stationId: String): DbStation?

    @Query("UPDATE station SET isFavorite = 1 WHERE stationId = :stationId")
    suspend fun makeFavorite(stationId: String)

    @Query("UPDATE station SET isFavorite = 0 WHERE stationId = :stationId")
    suspend fun removeFavorite(stationId: String)

    @Insert
    suspend fun insertStations(stations: List<DbStation>)

    @Query("DELETE FROM station")
    suspend fun deleteStations()

}