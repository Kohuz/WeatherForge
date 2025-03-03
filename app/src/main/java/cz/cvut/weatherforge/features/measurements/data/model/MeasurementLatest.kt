package cz.cvut.weatherforge.features.measurements.data.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MeasurementLatest(
    val stationId: String,
    val element: String,
    val timestamp: LocalDateTime,
    val value: Double?,
    val flag: String? = null,
    val quality: Double?
)
