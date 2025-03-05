//package cz.cvut.weatherforge.features.measurements.data.api
//
//import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
//import cz.cvut.weatherforge.features.stations.data.model.Station
//
//interface MeasurementRemoteDataSource {
//    suspend fun getStations(): List<Station>
//    suspend fun getClosest(lat: Float, long: Float, count: Int): List<Station>
//    suspend fun getStation(stationId: String): Station?
//    suspend fun getElementsCodelist(): List<ElementCodelistItem>
//
//
//}