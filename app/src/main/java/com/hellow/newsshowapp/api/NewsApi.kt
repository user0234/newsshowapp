package com.hellow.newsshowapp.api

import com.hellow.newsshowapp.models.NewsResponse
import com.hellow.newsshowapp.utils.Constants.Companion.API_Key
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

  @GET("v2/top-headlines")
  suspend fun getBreakingNews(
      @Query("country")
      countryCode:String = "us",
      @Query("page")
      pageNumber: Int = 1,
      @Query("apiKey")
      apiKey:String = API_Key
  ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery:String ,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey:String = API_Key
    ): Response<NewsResponse>

}