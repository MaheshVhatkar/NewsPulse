package com.example.mynewsapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mynewsapplication.apiCalll.Article
import com.example.mynewsapplication.callback.ShowArticleCallback
import com.example.mynewsapplication.repository.NewsRepository
import com.example.mynewsapplication.roomdb.NewsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowArticleViewModel(application: Application) : AndroidViewModel(application) {

    var title = MutableLiveData<String>()
    var webLink = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var isArticleSaved = MutableLiveData<Boolean>()
    private val showArticleCallback : ShowArticleCallback? = null
    private val newsRepository : NewsRepository by lazy {
        NewsRepository.getInstance(application.applicationContext)
    }

    fun webLinkClickListener() {
        showArticleCallback?.webLinkClicked()
    }
    private fun createNewsEntityObject(article: Article) : NewsEntity{
        return NewsEntity(
            article.author,
            article.title ?: "",
            article.description,
            article.url,
            article.urlToImage,
            article.publishedAt,
            article.content
        )
    }
    fun isArticleAlreadySaved(article: Article){
        viewModelScope.launch(Dispatchers.IO) {
            isArticleSaved.postValue( newsRepository.isArticleExists(article))
        }
    }

    fun saveArticleOffline(article : Article) {
        viewModelScope.launch(Dispatchers.IO) {
            newsRepository.insertNews(createNewsEntityObject(article))
        }
    }

    fun removeArticleOffline(article: Article) {
        viewModelScope.launch(Dispatchers.IO) {
            newsRepository.deleteNews(createNewsEntityObject(article))
        }
    }

}