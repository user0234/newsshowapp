package com.hellow.newsshowapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hellow.newsshowapp.repository.NewsRepository

class ViewModelProviderFactory(
    val app:Application,
    private val newsRepository: NewsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app,newsRepository) as T
    }
}