package cz.cvut.weatherforge.features.stations.data.model

data class StationsResult(
    val stations: List<Station>,
    val isSuccess: Boolean
)

data class StationResult(
    val station: Station?,
    val isSuccess: Boolean
)

data class ElementsCodelistResult(
    val elements: List<ElementCodelistItem>,
    val isSuccess: Boolean
)



