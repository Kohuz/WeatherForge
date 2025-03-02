package cz.cvut.weatherforge.features.stations.data.api

import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station
import cz.cvut.weatherforge.features.stations.data.model.StationElement
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StationApiDescription {
    @GET("stations/closest")
    suspend fun getClosest(@Query("lat") lat: Float,
                           @Query("long") long: Float,
                           @Query("count") count: Int): List<Station>
    @GET("stations")
    suspend fun getStations(): List<Station>

    @GET("stations/{id}")
    suspend fun getStation(@Path("id") id: String): Station?

    @GET("elementsCodelist")
    suspend fun getElementsCodelist(): List<ElementCodelistItem>
}