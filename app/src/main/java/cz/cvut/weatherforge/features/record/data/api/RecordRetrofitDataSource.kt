package cz.cvut.weatherforge.features.record.data.api

import cz.cvut.weatherforge.features.record.data.model.RecordStats
import cz.cvut.weatherforge.features.record.data.model.StationRecord
import retrofit2.http.Query

class RecordRetrofitDataSource(private val apiDescription: RecordApiDescription):
    RecordRemoteDataSource {
    override suspend fun getAllTimeStationRecords(id: String): List<RecordStats> {
        return apiDescription.getAllTimeStationRecords(id)
    }
    override suspend fun getAllTimeRecords(): List<RecordStats> {
        return apiDescription.getAllTimeRecords()
    }


}