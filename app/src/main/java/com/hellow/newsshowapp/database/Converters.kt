package com.hellow.newsshowapp.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.hellow.newsshowapp.models.Source

@ProvidedTypeConverter
class Converters {

    @TypeConverter
    fun fromSource(source: Source):String {
        return source.name
    }

    @TypeConverter
    fun nameToSource(name: String):Source {
        return Source(name,name)
    }
}