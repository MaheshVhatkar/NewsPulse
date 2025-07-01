package com.example.mynewsapplication.repository

import android.content.Context
import com.example.mynewsapplication.apiCalll.Article
import com.example.mynewsapplication.roomdb.NewsEntity
import com.example.mynewsapplication.roomdb.NewsRoomDatabase

class NewsRepository private constructor(context: Context) {

    private val newsRoomDatabase = NewsRoomDatabase.getInstance(context)
    private val newsDao = newsRoomDatabase.getDao()

    suspend fun insertNews(news : NewsEntity) {
        newsDao.insert(news)
    }

    suspend fun getAllNews() : List<NewsEntity>{
        return newsDao.getAll()
    }

    suspend fun deleteNews(news : NewsEntity) {
        newsDao.delete(news.title ?: "")
    }

    suspend fun deleteAllNews() {
        newsDao.deleteAll()
    }
    suspend fun isArticleExists(article: Article) : Boolean{
        return newsDao.isExists(article.title ?: "")
    }

    companion object {
        @Volatile
        private var INSTANCE : NewsRepository? = null
        fun getInstance(context: Context) : NewsRepository {
            val instance = INSTANCE?: synchronized(this) {
                NewsRepository(context)
            }
            INSTANCE = instance
            return instance
        }
    }
}