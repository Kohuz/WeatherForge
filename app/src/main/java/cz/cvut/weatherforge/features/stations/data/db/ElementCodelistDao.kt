package com.kozubek.livesport.features.sportEntries.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cz.cvut.weatherforge.features.stations.data.db.DbStation
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem

@Dao
interface ElementCodelistDao {
    @Query("SELECT * FROM elementCodelist")
    suspend fun getElements(): List<ElementCodelistItem>

    @Insert
    suspend fun insertElements(stations: List<ElementCodelistItem>)

    @Query("DELETE FROM elementCodelist")
    suspend fun deleteAll()

}