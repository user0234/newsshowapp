package com.hellow.newsshowapp.repository

import com.hellow.newsshowapp.api.RetrofitInstance
import com.hellow.newsshowapp.database.ArticleDataBase
import com.hellow.newsshowapp.models.Article
import retrofit2.Retrofit

class NewsRepository(
    val db:ArticleDataBase
) {
    suspend fun getBreakingNews(countryCode:String,pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery:String, pageNumber:Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article: Article) =  db.articleDao().insert(article)

    fun getSavedNews() = db.articleDao().getAllArticles()

    suspend fun delete(article: Article) = db.articleDao().deleteArticle(article)
}