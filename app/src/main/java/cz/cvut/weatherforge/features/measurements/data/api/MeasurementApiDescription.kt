package cz.cvut.weatherforge.features.measurements.data.api

import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementMonthly
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementYearly
import cz.cvut.weatherforge.features.record.data.model.ValueStats
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MeasurementApiDescription {

    @GET("/measurements/{stationId}/daily")
    suspend fun getMeasurementsDaily(
        @Path("stationId") stationId: String,
        @Query("dateFrom") dateFrom: String,
        @Query("dateTo") dateTo: String,
        @Query("element") element: String
    ): List<MeasurementDaily>

    @GET("/measurements/{stationId}/monthly")
    suspend fun getMeasurementsMonthly(
        @Path("stationId") stationId: String,
        @Query("dateFrom") dateFrom: String,
        @Query("dateTo") dateTo: String,
        @Query("element") element: String
    ): List<MeasurementMonthly>

    @GET("/measurements/{stationId}/yearly")
    suspend fun getMeasurementsYearly(
        @Path("stationId") stationId: String,
        @Query("dateFrom") dateFrom: String,
        @Query("dateTo") dateTo: String,
        @Query("element") element: String
    ): List<MeasurementYearly>

    @GET("/measurements/{stationId}/statsDayLongTerm")
    suspend fun getStatsDayLongTerm(
        @Path("stationId") stationId: String,
        @Query("date") date: String
    ): List<ValueStats>

    @GET("/measurements/{stationId}/measurementsDayAndMonth")
    suspend fun getMeasurementsDayAndMonth(
        @Path("stationId") stationId: String,
        @Query("date") date: String
    ): List<MeasurementDaily>

    @GET("/measurements/{stationId}/measurementsMonth")
    suspend fun getMeasurementsMonth(
        @Path("stationId") stationId: String,
        @Query("date") date: String
    ): List<MeasurementMonthly>

    @GET("/measurements/{stationId}/statsDay")
    suspend fun getStatsDay(
        @Path("stationId") stationId: String,
        @Query("date") date: String
    ): List<MeasurementDaily>
}