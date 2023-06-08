package com.hellow.newsshowapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hellow.newsshowapp.models.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article):Long

    @Query("Select * From article")
    fun getAllArticles():LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
   // TODO add pagination to room db
}
