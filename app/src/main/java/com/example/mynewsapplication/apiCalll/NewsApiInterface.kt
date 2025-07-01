package com.example.mynewsapplication.apiCalll


import com.example.mynewsapplication.utils.ApiString
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiInterface {

    @GET(ApiString.endPoint)
    suspend fun callNewsApi(@Query(ApiString.queryCountry) q : String,
                            @Query(ApiString.queryCategory) from : String,
                            @Query(ApiString.queryApiKey) apiKey : String) : Response<NewsResponse>
}