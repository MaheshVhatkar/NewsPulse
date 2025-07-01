package com.example.mynewsapplication.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mynewsapplication.utils.RoomString

@Entity(RoomString.tableName)
data class NewsEntity(
    val author: String?,
    @PrimaryKey(autoGenerate = false)
    val title: String,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?,
)


