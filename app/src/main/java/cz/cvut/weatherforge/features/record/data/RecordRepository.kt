package cz.cvut.weatherforge.features.record.data

import android.util.Log
import com.kozubek.livesport.features.sportEntries.data.StationLocalDataSource
import cz.cvut.weatherforge.features.record.data.api.RecordRemoteDataSource
import cz.cvut.weatherforge.features.record.data.model.RecordStats
import cz.cvut.weatherforge.features.record.data.model.RecordsResult
import cz.cvut.weatherforge.features.record.data.model.StationRecord
import cz.cvut.weatherforge.features.record.data.model.StatsResult
import cz.cvut.weatherforge.features.stations.data.api.StationRemoteDataSource
import cz.cvut.weatherforge.features.stations.data.model.StationResult
import cz.cvut.weatherforge.features.stations.data.model.StationsResult

class RecordRepository(
    private val recordRemoteDataSource: RecordRemoteDataSource,
) {
    suspend fun getDayStationRecords(id: String): RecordsResult{
        return try {
            val records = recordRemoteDataSource.getDayStationRecords(id)
            RecordsResult(records, isSuccess = true)
        } catch (t: Throwable) {
            Log.e("RecordRepository", "Error fetching records: ${t.message}")
            RecordsResult(emptyList(), isSuccess = false)
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
}