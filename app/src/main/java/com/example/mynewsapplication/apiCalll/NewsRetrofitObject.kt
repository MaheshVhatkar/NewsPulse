package com.example.mynewsapplication.apiCalll

import com.example.mynewsapplication.utils.ApiString
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NewsRetrofitObject {

    private val httpInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient().newBuilder()
        .addInterceptor(httpInterceptor)
        .build()

    val newsApiInterface : NewsApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(ApiString.baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiInterface::class.java)
    }
}