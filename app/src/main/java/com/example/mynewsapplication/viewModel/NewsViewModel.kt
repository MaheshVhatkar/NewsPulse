package com.example.mynewsapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mynewsapplication.utils.ApiString
import com.example.mynewsapplication.apiCalll.Article
import com.example.mynewsapplication.apiCalll.NewsRetrofitObject
import com.example.mynewsapplication.apiCalll.Source
import com.example.mynewsapplication.callback.NewsCallback
import com.example.mynewsapplication.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    val newsLiveList = MutableLiveData<List<Article>>()
    var newsList = listOf<Article>()
    var newsCallback : NewsCallback? = null
    var searchBarText = MutableLiveData<String>()
    var isOfflineMode = MutableLiveData<Boolean>()
    val offlineNewsLiveList = MutableLiveData<List<Article>>()
    private val newsRepository : NewsRepository by lazy {
        NewsRepository.getInstance(application.applicationContext) }

    fun callNewsApi() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NewsRetrofitObject.newsApiInterface.callNewsApi(
                    ApiString.country,
                    ApiString.category,
                    ApiString.apiKey
                )
                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        newsList = response.body()?.articles ?: emptyList()
                        updateNewsList(response.body()?.articles ?: emptyList())
                        newsCallback?.onApiSuccess()
                    }

                }else {
                    newsCallback?.onApiFail(response.errorBody().toString())
                }
            }catch (exception : Exception) {
                newsCallback?.onApiFail(exception.message.toString())
            }
        }
    }

    fun filterNewsList(keyWord : String) {
        if (isOfflineMode.value == true) {
            val filteredList = offlineNewsLiveList.value?.filter { it.title?.contains(keyWord) ?: false }
            updateNewsList(filteredList?: emptyList())
        }else {
            val filteredList = newsList.filter { it.title?.contains(keyWord) ?: false }
            updateNewsList(filteredList)
        }
    }

    fun updateNewsList(list : List<Article>) {
        newsLiveList.value = list
    }

    fun loadOfflineList() {
        viewModelScope.launch(Dispatchers.IO) {
            val offlineNewsEntityList = newsRepository.getAllNews()
            val offlineArticles = offlineNewsEntityList.map { it -> Article(
                Source("", ""),
                it.author,
                it.title,
                it.description,
                it.url,
                it.urlToImage,
                it.publishedAt,
                it.content
            ) }
            offlineNewsLiveList.postValue(offlineArticles)
        }
    }
}