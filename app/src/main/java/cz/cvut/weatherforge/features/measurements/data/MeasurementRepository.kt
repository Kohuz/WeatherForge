package cz.cvut.weatherforge.features.measurements.data

import android.util.Log
import cz.cvut.weatherforge.features.measurements.data.api.MeasurementRemoteDataSource
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDaily
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementDailyResult
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementMonthly
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementMonthlyResult
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementYearly
import cz.cvut.weatherforge.features.measurements.data.model.MeasurementYearlyResult
import cz.cvut.weatherforge.features.measurements.data.model.ValueStatsResult
import cz.cvut.weatherforge.features.record.data.model.ValueStats
import cz.cvut.weatherforge.features.stations.data.api.StationRemoteDataSource
import cz.cvut.weatherforge.features.stations.data.model.StationsResult

class MeasurementRepository(
    private val remoteDataSource: MeasurementRemoteDataSource
) {
    suspend fun getDailyMeasurements(
        stationId: String,
        dateFrom: String,
        dateTo: String,
        element: String
    ): MeasurementDailyResult {
        return try {
            val measurements = remoteDataSource.getMeasurementsDaily(stationId, dateFrom, dateTo, element)
            MeasurementDailyResult(measurements.filter { it.vtype == "AVG" }, isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            MeasurementDailyResult(emptyList(), isSuccess = false)
        }
    }

    suspend fun getMonthlyMeasurements(
        stationId: String,
        dateFrom: String,
        dateTo: String,
        element: String
    ): MeasurementMonthlyResult {
        return try {
            val measurements = remoteDataSource.getMeasurementsMonthly(stationId, dateFrom, dateTo, element)
            MeasurementMonthlyResult(measurements, isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            MeasurementMonthlyResult(emptyList(), isSuccess = false)
        }
    }

    suspend fun getYearlyMeasurements(
        stationId: String,
        dateFrom: String,
        dateTo: String,
        element: String
    ): MeasurementYearlyResult {
        return try {
            val measurements = remoteDataSource.getMeasurementsYearly(stationId, dateFrom, dateTo, element)
            MeasurementYearlyResult(measurements, isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            MeasurementYearlyResult(emptyList(), isSuccess = false)
        }
    }

    suspend fun getStatsDayLongTerm(
        stationId: String,
        date: String
    ): ValueStatsResult {
        return try {
            val valueStats = remoteDataSource.getStatsDayLongTerm(stationId, date)
            ValueStatsResult(valueStats, isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            ValueStatsResult(null, isSuccess = false)
        }
    }

    suspend fun getMeasurementsDayAndMonth(
        stationId: String,
        date: String
    ): MeasurementDailyResult {
        return try {
            val measurements = remoteDataSource.getMeasurementsDayAndMonth(stationId, date)
            MeasurementDailyResult(measurements, isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            MeasurementDailyResult(emptyList(), isSuccess = false)
        }
    }

    suspend fun getMeasurementsMonth(
        stationId: String,
        date: String
    ): MeasurementMonthlyResult{
        return try {
            val measurements = remoteDataSource.getMeasurementsMonth(stationId, date)
            MeasurementMonthlyResult(measurements, isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            MeasurementMonthlyResult(emptyList(), isSuccess = false)
        }
    }

    suspend fun getStatsDay(
        stationId: String,
        date: String
    ): MeasurementDailyResult {
        return try {
            val measurements = remoteDataSource.getStatsDay(stationId, date)
            MeasurementDailyResult(measurements, isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            MeasurementDailyResult(emptyList(), isSuccess = false)
        }
    }
}