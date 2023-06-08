package com.hellow.newsshowapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hellow.newsshowapp.NewsApplication
import com.hellow.newsshowapp.models.Article
import com.hellow.newsshowapp.models.NewsResponse
import com.hellow.newsshowapp.repository.NewsRepository
import com.hellow.newsshowapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    app: Application,
     val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse:NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse:NewsResponse? = null

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
         safeBreakingNews(countryCode)

    }

    fun searchNews(searchQuery: String) =  viewModelScope.launch {
        safeSearchNews(searchQuery)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
          if(response.isSuccessful){
              if (response.body() != null) {
                  breakingNewsPage++
                   if(breakingNewsResponse == null){
                       breakingNewsResponse = response.body()
                   }else{
                       val oldArticles = breakingNewsResponse!!.articles
                       val newArticles = response.body()!!.articles
                       oldArticles.addAll(newArticles)
                       

                   }
                  return Resource.Success(breakingNewsResponse?: response.body())
              }
          }

        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if(response.isSuccessful){
            if (response.body() != null) {
                searchNewsPage++
                if(searchNewsResponse == null){
                    searchNewsResponse = response.body()
                }else{
                    val oldArticles = searchNewsResponse!!.articles
                    val newArticles = response.body()!!.articles
                    oldArticles.addAll(newArticles)


                }
                return Resource.Success(searchNewsResponse?: response.body())
            }
        }

        return Resource.Error(response.message())
    }

    fun getSavedArticle() = newsRepository.getSavedNews()

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.delete(article)
    }

    private suspend fun safeSearchNews(query: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = newsRepository.searchNews(query, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else{
                searchNews.postValue(Resource.Error("Check your internet "))
            }

        } catch (t:Throwable){
            searchNews.postValue(Resource.Error("an Error has occurred"))
        }
    }

    private suspend fun safeBreakingNews(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
            val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
            breakingNews.postValue(handleBreakingNewsResponse(response))
            } else{
                breakingNews.postValue(Resource.Error("Please Check Internet Connection"))
            }

        } catch (t:Throwable){
           breakingNews.postValue(Resource.Error("an Error has occurred"))
        }
    }


    private fun hasInternetConnection():Boolean {
        val connectivityManager =  getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
       val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities =  connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when{
            capabilities.hasTransport(TRANSPORT_WIFI) -> true
            capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}