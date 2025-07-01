package com.example.mynewsapplication.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mynewsapplication.utils.RoomString

@Database(entities = [NewsEntity::class], version = 1)
abstract class NewsRoomDatabase : RoomDatabase() {

    abstract fun getDao() : NewsDao

    companion object {
        @Volatile
        private var INSTANCE : NewsRoomDatabase? = null
        fun getInstance(context: Context) : NewsRoomDatabase {
            val instance = INSTANCE?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    NewsRoomDatabase::class.java,
                    RoomString.dbName
                ).build()
            }
            INSTANCE = instance
            return instance
        }
    }
}