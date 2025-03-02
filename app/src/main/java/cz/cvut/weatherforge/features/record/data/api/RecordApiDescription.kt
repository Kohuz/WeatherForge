package cz.cvut.weatherforge.features.record.data.api

import cz.cvut.weatherforge.features.record.data.model.RecordStats
import cz.cvut.weatherforge.features.record.data.model.StationRecord
import retrofit2.http.GET
import retrofit2.http.Path

interface RecordApiDescription {
    @GET("/stationDayRecords/{stationId}")
    suspend fun getDayStationRecords(@Path("id") id: String): List<Pair<StationRecord, StationRecord>>

    @GET("stationAllTimeRecords/{id}")
    suspend fun getAllTimeStationRecords(@Path("id") id: String): List<RecordStats>
}