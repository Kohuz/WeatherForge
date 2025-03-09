package cz.cvut.weatherforge.features.record.data.api

import cz.cvut.weatherforge.features.record.data.model.RecordStats
import cz.cvut.weatherforge.features.record.data.model.StationRecord

interface RecordRemoteDataSource {
    suspend fun getDayStationRecords(id: String):  List<RecordStats>
    suspend fun getAllTimeStationRecords(id: String): List<RecordStats>
    suspend fun getDayRecords(): List<RecordStats>
    suspend fun getAllTimeRecords(): List<RecordStats>


}