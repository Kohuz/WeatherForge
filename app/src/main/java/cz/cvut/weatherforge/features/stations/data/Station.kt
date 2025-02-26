package cz.cvut.weatherforge.features.stations.data

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
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
fun Station.isActive(): Boolean {
    val activeEndDate = LocalDateTime.parse("3999-12-31T23:59:00.000000")
        .toInstant(TimeZone.UTC)
    val stationEndDate = this.endDate.toInstant(TimeZone.UTC)
    return stationEndDate == activeEndDate
}


