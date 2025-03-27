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
            val filteredMeasurements = if (element == "T" || element == "F") {
                measurements.filter { it.vtype == "AVG" }
            } else {
                measurements
            }
            MeasurementDailyResult(filteredMeasurements, isSuccess = true)
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

            val filteredMeasurements = when (element) {
                "T" -> measurements.filter { it.timeFunction == "AVG" && it.mdFunction == "AVG" }
                "TMA" -> measurements.filter { it.mdFunction == "MAX" }
                "TMI" -> measurements.filter { it.mdFunction == "MIN" }
                "F" -> measurements.filter { it.timeFunction == "AVG" && it.mdFunction == "AVG" }
                "FMAX" -> measurements.filter { it.mdFunction == "MAX" }
                "SCE" -> measurements.filter { it.mdFunction == "GE(1)" }
                "SNO" -> measurements.filter { it.mdFunction == "GE(1)" }
                else -> measurements
            }

            MeasurementMonthlyResult(filteredMeasurements, isSuccess = true)
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

            val filteredMeasurements = when (element) {
                "T" -> measurements.filter { it.timeFunction == "AVG" && it.mdFunction == "AVG" }
                "TMA" -> measurements.filter { it.mdFunction == "MAX" }
                "TMI" -> measurements.filter { it.mdFunction == "MIN" }
                "F" -> measurements.filter { it.timeFunction == "AVG" && it.mdFunction == "AVG" }
                "FMAX" -> measurements.filter { it.mdFunction == "MAX" }
                "SCE" -> measurements.filter { it.mdFunction == "GE(1)" }
                "SNO" -> measurements.filter { it.mdFunction == "GE(1)" }
                else -> measurements
            }
            MeasurementYearlyResult(filteredMeasurements, isSuccess = true)
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
            ValueStatsResult(emptyList(), isSuccess = false)
        }
    }

    suspend fun getMeasurementsDayAndMonth(
        stationId: String,
        date: String,
        element: String
    ): MeasurementDailyResult {
        return try {
            val measurements = remoteDataSource.getMeasurementsDayAndMonth(stationId, date, element)

            val filteredMeasurements = if (element == "T" || element == "F") {
                measurements.filter { it.vtype == "AVG" }
            } else {
                measurements
            }

            MeasurementDailyResult(filteredMeasurements, isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            MeasurementDailyResult(emptyList(), isSuccess = false)
        }
    }

    suspend fun getMeasurementsMonth(
        stationId: String,
        date: String,
        element: String
    ): MeasurementMonthlyResult{
        return try {
            val measurements = remoteDataSource.getMeasurementsMonth(stationId, date, element)
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
            val filteredMeasurements = measurements.filter { measurement ->
                if (measurement.element == "T" || measurement.element == "F") {
                    measurement.vtype == "AVG"
                } else {
                    true
                }
            }
            MeasurementDailyResult(filteredMeasurements, isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            MeasurementDailyResult(emptyList(), isSuccess = false)
        }
    }

    suspend fun getMeasurementsTop(
        stationId: String?,
        date: String?,
        element: String
    ): MeasurementDailyResult {
        return try {
            val measurements = remoteDataSource.getMeasurementsTop(stationId, date, element)
            val filteredMeasurements = if (element == "T" || element == "F") {
                measurements.filter { it.vtype == "AVG" }
            } else {
                measurements
            }
            MeasurementDailyResult(filteredMeasurements, isSuccess = true)
        } catch (t: Throwable) {
            Log.v("api", t.toString())
            MeasurementDailyResult(emptyList(), isSuccess = false)
        }
    }
}