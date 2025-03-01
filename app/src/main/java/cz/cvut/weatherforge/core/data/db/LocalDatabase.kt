package cz.cvut.weatherforge.core.data.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kozubek.livesport.features.sportEntries.data.db.StationDao
import cz.cvut.weatherforge.features.stations.data.db.Converters
import cz.cvut.weatherforge.features.stations.data.db.DbStation


@Database(version = 1, entities = [DbStation::class])
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun stationDao(): StationDao

    companion object {
        fun instance(context: Context): LocalDatabase {
            return Room.databaseBuilder(context,
                LocalDatabase::class.java, "livesport.db").build()
        }
    }
}
