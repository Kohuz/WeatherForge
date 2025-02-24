package cz.cvut.weatherforge.features.stations.data

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable


@Serializable
data class Station(
    val stationId: String,
    val code: String,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime,
    val location: String,
    val longitude: Double,
    val latitude: Double,
    val elevation: Double,
)
