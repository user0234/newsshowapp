package com.hellow.newsshowapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hellow.newsshowapp.models.Article

@Database(entities = [Article::class],version = 1,exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArticleDataBase : RoomDatabase() {

  abstract fun articleDao():ArticleDao

companion object {
    @Volatile
    private var instance: ArticleDataBase? = null
    private val LOCK = Any()

    // singleton pattern
    operator fun invoke(context: Context) = instance ?: synchronized(this) {
         val INSTANCE =  Room.databaseBuilder(
             context.applicationContext,
             ArticleDataBase::class.java,
             "article_database.db"
         ).addTypeConverter(Converters()).build()
        instance = INSTANCE

       instance
    }


}

}