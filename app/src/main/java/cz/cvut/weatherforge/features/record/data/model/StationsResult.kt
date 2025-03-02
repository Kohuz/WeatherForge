package cz.cvut.weatherforge.features.record.data.model

data class RecordsResult(
    val station: List<RecordStats>?,
    val isSuccess: Boolean
)



