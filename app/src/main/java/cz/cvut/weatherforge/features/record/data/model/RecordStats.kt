package cz.cvut.weatherforge.features.record.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RecordStats(
    val highest: StationRecord?,
    val lowest: StationRecord?,
    val average: Double?
)

@Serializable
data class ValueStats(
    val highest: Double?,
    val lowest: Double?,
    val average: Double?
)