package cz.cvut.weatherforge.features.record.data

import android.util.Log
import cz.cvut.weatherforge.features.record.data.api.RecordRemoteDataSource
import cz.cvut.weatherforge.features.record.data.model.StatsResult

class RecordRepository(
    private val recordRemoteDataSource: RecordRemoteDataSource,
) {
    suspend fun getDayStationRecords(id: String): StatsResult{
        return try {
            val records = recordRemoteDataSource.getDayStationRecords(id)
            StatsResult(records, isSuccess = true)
        } catch (t: Throwable) {
            Log.e("RecordRepository", "Error fetching records: ${t.message}")
            StatsResult(emptyList(), isSuccess = false)
        }
    }
    suspend fun getAllTimeStationRecords(id: String): StatsResult {
        return try {
            val recordsStats = recordRemoteDataSource.getAllTimeStationRecords(id)
            StatsResult(recordsStats, isSuccess = true)
        } catch (t: Throwable) {
            Log.e("RecordRepository", "Error fetching records: ${t.message}")
            StatsResult(emptyList(), isSuccess = false)
        }
    }

    suspend fun getDayRecords(): StatsResult {
        return try {
            val recordsStats = recordRemoteDataSource.getDayRecords()
            StatsResult(recordsStats, isSuccess = true)
        }
        catch (t: Throwable) {
            Log.e("RecordRepository", "Error fetching records: ${t.message}")
            StatsResult(emptyList(), isSuccess = false)
        }
    }

    suspend fun getAllTimeRecords(): StatsResult {
        return try {
            val recordsStats = recordRemoteDataSource.getAllTimeRecords()
            StatsResult(recordsStats, isSuccess = true)
        }
        catch (t: Throwable) {
            Log.e("RecordRepository", "Error fetching records: ${t.message}")
            StatsResult(emptyList(), isSuccess = false)
        }

    }

}