package cz.cvut.weatherforge.features.stations.data.db

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.toJavaLocalDateTime()?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            java.time.LocalDateTime.parse(it, formatter).toKotlinLocalDateTime()
        }
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.toJavaLocalDate()?.format(formatter)
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let {
            java.time.LocalDate.parse(it, formatter).toKotlinLocalDate()
        }
    }
}