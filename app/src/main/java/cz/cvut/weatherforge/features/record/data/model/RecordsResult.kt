package cz.cvut.weatherforge.features.record.data.model

data class StatsResult(
    val stats: List<RecordStats>,
    val isSuccess: Boolean
)

data class RecordsResult(
    val records: List<Pair<StationRecord, StationRecord>>,
    val isSuccess: Boolean
)



