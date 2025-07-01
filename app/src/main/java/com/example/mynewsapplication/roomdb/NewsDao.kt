package com.example.mynewsapplication.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsDao {

    @Insert(NewsEntity::class, OnConflictStrategy.REPLACE)
    suspend fun insert(item : NewsEntity)

    @Query("SELECT * FROM saved_news_table")
    suspend fun getAll() : List<NewsEntity>

    @Query("DELETE FROM saved_news_table WHERE title = :title")
    suspend fun delete(title : String)

    @Query("DELETE FROM saved_news_table")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT 1 FROM saved_news_table WHERE title = :title)")
    suspend fun isExists(title : String) : Boolean

}