package cz.cvut.weatherforge.features.stations.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "elementCodelist")
data class DbElementCodelistItem (
    @PrimaryKey
    val abbreviation: String,
    val name: String,
    val unit: String
)

