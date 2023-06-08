package com.hellow.newsshowapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hellow.newsshowapp.R
import com.hellow.newsshowapp.database.ArticleDataBase
import com.hellow.newsshowapp.repository.NewsRepository


// TODO add splash api to app
// TODO add widget for top 5 fav news in a interactable list
class NewsActivity : AppCompatActivity() {

      lateinit var viewModel: NewsViewModel
       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val newsRepository = NewsRepository(ArticleDataBase(this)!!)

        val viewModelProviderFactory = ViewModelProviderFactory(application,newsRepository)

        viewModel = ViewModelProvider(this,viewModelProviderFactory)[NewsViewModel::class.java]

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(navView(),navController)
    }

    private fun navView(): BottomNavigationView {
        return findViewById(R.id.bottomNavigationView)
    }
}