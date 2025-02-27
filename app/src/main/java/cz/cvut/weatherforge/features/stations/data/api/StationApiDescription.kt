package cz.cvut.weatherforge.features.stations.data.api

import cz.cvut.weatherforge.features.stations.data.GeoJSONStationCollection
import cz.cvut.weatherforge.features.stations.data.Station
import retrofit2.http.GET
import retrofit2.http.Query

interface StationApiDescription {
    @GET("geostations")
    suspend fun getGeoStations(): GeoJSONStationCollection

    @GET("stations")
    suspend fun getStations(): List<Station>
}