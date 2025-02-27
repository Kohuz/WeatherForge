package cz.cvut.weatherforge.features.stations.data

data class StationResult(
    val stations: List<Station>,
    val isSuccess: Boolean
)

data class GeoStationResult(
    val stations: GeoJSONStationCollection,
    val isSuccess: Boolean
)