package cz.cvut.weatherforge.features.stations.data

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


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

@Serializable
data class GeoJSONFeatureCollection(
    val type: String = "FeatureCollection",
    val features: List<GeoJSONFeature>
)

@Serializable
data class GeoJSONFeature(
    val type: String = "Feature",
    val geometry: GeoJSONGeometry,
    val properties: StationProperties
)

@Serializable
data class GeoJSONGeometry(
    val type: String = "Point",
    val coordinates: List<Double> // [longitude, latitude]
)

@Serializable
data class StationProperties(
    val stationId: String,
    val code: String,
    val startDate: String,
    val endDate: String,
    val location: String,
    val elevation: Double
)

fun parseGeoJSON(geoJSONString: String): GeoJSONFeatureCollection {
    return Json.decodeFromString(geoJSONString)
}
