package cz.cvut.weatherforge.features.record.data.api

import cz.cvut.weatherforge.features.record.data.model.RecordStats
import cz.cvut.weatherforge.features.record.data.model.StationRecord

class RecordRetrofitDataSource(private val apiDescription: RecordApiDescription):
    RecordRemoteDataSource {
    override suspend fun getDayStationRecords(id: String):  List<Pair<StationRecord, StationRecord>>{
        return apiDescription.getDayStationRecords(id)
    }
    override suspend fun getAllTimeStationRecords(id: String): List<RecordStats> {
        return apiDescription.getAllTimeStationRecords(id)
    }
}