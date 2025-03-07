package cz.cvut.weatherforge.features.measurements.data.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable


@Serializable
data class MeasurementDaily(
    val stationId: String,
    val element: String,
    val date: LocalDate,
    val value: Double?,
    val vtype: String,
    val flag: String? = null,
    val quality: Double?,
    val schedule: String? = null,
)
