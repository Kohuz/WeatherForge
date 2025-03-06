package cz.cvut.weatherforge.features.measurements.data.api

import cz.cvut.weatherforge.features.measurements.data.api.MeasurementApiDescription
import cz.cvut.weatherforge.features.measurements.data.api.MeasurementRemoteDataSource
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementMonthly
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementYearly
import cz.cvut.weatherforge.features.record.data.model.ValueStats

class MeasurementRetrofitDataSource(
    private val apiDescription: MeasurementApiDescription
) : MeasurementRemoteDataSource {

    override suspend fun getMeasurementsDaily(
        stationId: String,
        dateFrom: String,
        dateTo: String,
        element: String
    ): List<MeasurementDaily> {
        return apiDescription.getMeasurementsDaily(stationId, dateFrom, dateTo, element)
    }

    override suspend fun getMeasurementsMonthly(
        stationId: String,
        dateFrom: String,
        dateTo: String,
        element: String
    ): List<MeasurementMonthly> {
        return apiDescription.getMeasurementsMonthly(stationId, dateFrom, dateTo, element)
    }

    override suspend fun getMeasurementsYearly(
        stationId: String,
        dateFrom: String,
        dateTo: String,
        element: String
    ): List<MeasurementYearly> {
        return apiDescription.getMeasurementsYearly(stationId, dateFrom, dateTo, element)
    }

    override suspend fun getStatsDayLongTerm(
        stationId: String,
        date: String
    ): ValueStats {
        return apiDescription.getStatsDayLongTerm(stationId, date)
    }

    override suspend fun getMeasurementsDayAndMonth(
        stationId: String,
        date: String
    ): List<MeasurementDaily> {
        return apiDescription.getMeasurementsDayAndMonth(stationId, date)
    }

    override suspend fun getMeasurementsMonth(
        stationId: String,
        date: String
    ): List<MeasurementMonthly> {
        return apiDescription.getMeasurementsMonth(stationId, date)
    }

    override suspend fun getStatsDay(
        stationId: String,
        date: String
    ): List<MeasurementDaily> {
        return apiDescription.getStatsDay(stationId, date)
    }
}