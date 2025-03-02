package cz.cvut.weatherforge.features.stations.data.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class StationElement(
    val stationId: String,
    val beginDate: LocalDateTime?,
    val endDate: LocalDateTime,
    val elementAbbreviation: String,
    val elementName: String,
    val unitDescription: String,
    val height: Double?,
    val schedule: String
)

@Serializable
data class ElementCodelistItem(
    val abbreviation: String,
    val name: String,
    val unit: String
)
