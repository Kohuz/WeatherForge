package cz.cvut.weatherforge.features.stations.data.api

import cz.cvut.weatherforge.features.stations.data.Station
import retrofit2.http.GET
import retrofit2.http.Query

interface StationApiDescription {
    @GET("stations")
    suspend fun getSportEntries(@Query("name", encoded = true)
                                name: String?,
                                @Query("elevationMin", encoded = true)
                                elevationMin: Number?,
                                @Query("elevationMax", encoded = true)
                                elevationMax: Number?,
                                @Query("active", encoded = true)
                                active: Boolean?): List<Station>
}