package my.weatherapp.core.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import my.weatherapp.core.model.Data

@Database(entities = [Data::class], version = 1)
abstract class Database : RoomDatabase() {

    abstract fun dao(): Dao


    companion object {
        private var INSTANSE: my.weatherapp.core.db.Database? = null

        fun init(context: Context) {
            if (INSTANSE == null)
                INSTANSE = Room.databaseBuilder(
                    context,
                    my.weatherapp.core.db.Database::class.java,
                    "data"
                )
                    .allowMainThreadQueries()
                    .build()
        }

        fun getDB(): my.weatherapp.core.db.Database = INSTANSE!!
    }

}