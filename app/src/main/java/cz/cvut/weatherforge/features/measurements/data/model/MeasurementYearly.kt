package cz.cvut.weatherforge.features.measurements.data.model

import kotlinx.serialization.Serializable


@Serializable
data class MeasurementYearly(
    val stationId: String,
    val element: String,
    val year: Int,
    val timeFunction: String,
    val mdFunction: String,
    val value: Double?,
    val flagRepeat: String? = null,
    val flagInterrupted: String? = null
)
