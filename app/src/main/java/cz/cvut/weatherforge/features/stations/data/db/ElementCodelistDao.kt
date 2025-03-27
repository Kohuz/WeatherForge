package cz.cvut.weatherforge.features.stations.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cz.cvut.weatherforge.features.stations.data.db.DbElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.db.DbStation
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem

@Dao
interface ElementCodelistDao {
    @Query("SELECT * FROM elementCodelist")
    suspend fun getElements(): List<DbElementCodelistItem>

    @Insert
    suspend fun insertElements(stations: List<DbElementCodelistItem>)

    @Query("DELETE FROM elementCodelist")
    suspend fun deleteAll()

}