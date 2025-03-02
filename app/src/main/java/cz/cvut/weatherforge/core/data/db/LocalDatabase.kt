package cz.cvut.weatherforge.core.data.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kozubek.livesport.features.sportEntries.data.db.ElementCodelistDao
import com.kozubek.livesport.features.sportEntries.data.db.StationDao
import cz.cvut.weatherforge.features.stations.data.db.Converters
import cz.cvut.weatherforge.features.stations.data.db.DbElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.db.DbStation


@Database(version = 2, entities = [DbStation::class, DbElementCodelistItem::class])
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun stationDao(): StationDao
    abstract fun elementCodelistDao(): ElementCodelistDao


    companion object {
        fun instance(context: Context): LocalDatabase {
            return Room.databaseBuilder(context,
                LocalDatabase::class.java, "localDatabase.db").fallbackToDestructiveMigration().build()
        }
    }
}
