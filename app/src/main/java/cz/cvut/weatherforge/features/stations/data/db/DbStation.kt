package cz.cvut.weatherforge.features.stations.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "station")
data class DbStation(
    @PrimaryKey val id: String,
    val stationId: String,
    val code: String,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime,
    val location: String,
    val longitude: Double,
    val latitude: Double,
    val elevation: Double,
)
