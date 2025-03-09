package cz.cvut.weatherforge.features.measurements.data.model

import cz.cvut.weatherforge.features.record.data.model.ValueStats
import cz.cvut.weatherforge.features.stations.data.model.Station


data class MeasurementDailyResult(
    val measurements: List<MeasurementDaily>,
    val isSuccess: Boolean
)

data class MeasurementMonthlyResult(
    val measurements: List<MeasurementMonthly>,
    val isSuccess: Boolean
)

data class MeasurementYearlyResult(
    val measurements: List<MeasurementYearly>,
    val isSuccess: Boolean
)

data class ValueStatsResult(
    val valueStats: List<ValueStats>,
    val isSuccess: Boolean
)