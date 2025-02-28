package cz.cvut.weatherforge.features.stations.data.api

import cz.cvut.weatherforge.features.stations.data.model.Station
import retrofit2.http.GET
import retrofit2.http.Path

interface StationApiDescription {
    @GET("stations")
    suspend fun getStations(): List<Station>

    @GET("stations/{id}")
    suspend fun getStation(@Path("id") id: String): Station?
}