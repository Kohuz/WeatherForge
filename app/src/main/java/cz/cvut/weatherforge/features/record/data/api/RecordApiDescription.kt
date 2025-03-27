package cz.cvut.weatherforge.features.record.data.api

import cz.cvut.weatherforge.features.record.data.model.RecordStats
import cz.cvut.weatherforge.features.record.data.model.StationRecord
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecordApiDescription {
    @GET("stationAllTimeRecords/{id}")
    suspend fun getAllTimeStationRecords(@Path("id") id: String): List<RecordStats>

    @GET("/recordsAllTime")
    suspend fun getAllTimeRecords(): List<RecordStats>
}