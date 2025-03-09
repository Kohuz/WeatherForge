package cz.cvut.weatherforge.features.measurements.data.api

import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementMonthly
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementYearly
import cz.cvut.weatherforge.features.record.data.model.ValueStats

interface MeasurementRemoteDataSource {
    suspend fun getMeasurementsDaily(
        stationId: String,
        dateFrom: String,
        dateTo: String,
        element: String
    ): List<MeasurementDaily>

    suspend fun getMeasurementsMonthly(
        stationId: String,
        dateFrom: String,
        dateTo: String,
        element: String
    ): List<MeasurementMonthly>

    suspend fun getMeasurementsYearly(
        stationId: String,
        dateFrom: String,
        dateTo: String,
        element: String
    ): List<MeasurementYearly>

    suspend fun getStatsDayLongTerm(
        stationId: String,
        date: String
    ): List<ValueStats>

    suspend fun getMeasurementsDayAndMonth(
        stationId: String,
        date: String
    ): List<MeasurementDaily>

    suspend fun getMeasurementsMonth(
        stationId: String,
        date: String
    ): List<MeasurementMonthly>

    suspend fun getStatsDay(
        stationId: String,
        date: String
    ): List<MeasurementDaily>
}