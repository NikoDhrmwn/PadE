package com.nrtxx.pade.db

import android.content.Context
import androidx.room.*
import com.nrtxx.pade.helper.Converter

@Database(entities = [History::class], version = 1)
@TypeConverters(Converter::class)
abstract class HistoryRoomDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private  var INSTANCE: HistoryRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context) : HistoryRoomDatabase {
            if (INSTANCE == null) {
                synchronized(HistoryRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, HistoryRoomDatabase::class.java, "history_database").build()
                }
            }
            return INSTANCE as HistoryRoomDatabase
        }
    }
}