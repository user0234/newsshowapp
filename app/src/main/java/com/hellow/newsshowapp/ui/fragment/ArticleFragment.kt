package com.hellow.newsshowapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.hellow.newsshowapp.R
import com.hellow.newsshowapp.ui.NewsActivity
import com.hellow.newsshowapp.ui.NewsViewModel

class ArticleFragment: Fragment( R.layout.fragment_article) {

    lateinit var viewModel:NewsViewModel
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        val article = args.article
        val webView: WebView =  view.findViewById(R.id.webView)
        webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        val fab:FloatingActionButton = view.findViewById(R.id.fab)
        fab.setOnClickListener{
            viewModel.saveArticle(article)
            Snackbar.make(view,"saved in storage",Snackbar.LENGTH_SHORT).show()



        }



    }
}