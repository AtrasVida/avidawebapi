package co.atrasvida.avidawebapi.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [MCash::class], version = 1)
abstract class AvidaAppDatabases : RoomDatabase() {

    companion object {
        private const val dataBaseName: String = "api_casher.db"
        var dbInstance: AvidaAppDatabases? = null
        @JvmStatic
        fun getInstance(): AvidaAppDatabases? {
            return dbInstance
        }
        @JvmStatic
        fun newInstance(context: Context): AvidaAppDatabases? {
            if (dbInstance == null) {
                synchronized(MCash::class.java) {
                    dbInstance =
                        Room.databaseBuilder(context, AvidaAppDatabases::class.java, dataBaseName)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return dbInstance
        }


    }


    public abstract fun mCashDao(): MCashDao


    fun destroyInstance() {
        dbInstance = null
    }


}