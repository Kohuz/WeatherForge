package cz.cvut.weatherforge.features.stations.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementLatest
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
    val stationElements: List<StationElement> = emptyList(),
    var stationLatestMeasurements: List<MeasurementLatest> = emptyList()
): ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(latitude, longitude)
    }

    override fun getTitle(): String {
        return this.location
    }

    override fun getSnippet(): String {
        return this.location
    }
    override fun getZIndex(): Float {
        return 1f
        }
}

class ClusterStation(
    lat: Double,
    lng: Double,
    title: String,
    snippet: String
) : ClusterItem {

    private val position: LatLng
    private val title: String
    private val snippet: String

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }

    override fun getZIndex(): Float {
        return 0f
    }

    init {
        position = LatLng(lat, lng)
        this.title = title
        this.snippet = snippet
    }
}



fun Station.isActive(): Boolean {
    val activeEndDate = LocalDateTime.parse("3999-12-31T23:59:00.000000")
        .toInstant(TimeZone.UTC)
    val stationEndDate = this.endDate.toInstant(TimeZone.UTC)
    return stationEndDate == activeEndDate
}

