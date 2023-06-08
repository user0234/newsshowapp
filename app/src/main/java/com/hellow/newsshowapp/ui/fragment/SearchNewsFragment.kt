package com.hellow.newsshowapp.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hellow.newsshowapp.R
import com.hellow.newsshowapp.adaptors.NewsAdaptor
import com.hellow.newsshowapp.ui.NewsActivity
import com.hellow.newsshowapp.ui.NewsViewModel
import com.hellow.newsshowapp.utils.Constants
import com.hellow.newsshowapp.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment: Fragment( R.layout.fragment_search_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdaptor: NewsAdaptor
    lateinit var rvSearchNews: RecyclerView
    lateinit var etSearch: TextView
    lateinit var paginationProgressBar:ProgressBar

    val TAG = "SearchNewsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
          paginationProgressBar = view.findViewById(R.id.paginationProgressBar)
          etSearch = view.findViewById(R.id.etSearch)
         rvSearchNews = view.findViewById(R.id.rvSearchNews)

        setUpRecyclerView(view)

        newsAdaptor.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article",article)
            }

            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,bundle
            )
        }
        var job:Job?= null
        etSearch.addTextChangedListener{ text ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                if (text != null) {
                    if(text.toString().isNotEmpty()){
                        viewModel.searchNews(text.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar(paginationProgressBar)
                    if (response.data != null) {
                        newsAdaptor.differ.submitList(response.data.articles.toList())
                        val totalPages = response.data.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if(isLastPage) {
                            rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar(paginationProgressBar)
                    if (response.message != null) {
                        Log.e(TAG,"error: ${response.message}")
                        showSnackBar("${response.message}",view)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar(paginationProgressBar)
                }
            }
        })
    }

    private fun hideProgressBar(paginationProgressBar: ProgressBar) {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(paginationProgressBar: ProgressBar) {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun showSnackBar(message:String,view: View){
        val snackBar = Snackbar
            .make(view,
                "$message", Snackbar.LENGTH_LONG)
        snackBar.setTextColor(Color.RED)
        snackBar.show()

    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true

            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPos = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount =  layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPos + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPos >= 0
            val isTotalMoreThenVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThenVisible && isScrolling
            if(shouldPaginate)
            {
                viewModel.searchNews(etSearch.text.toString())
                isScrolling = false
            } else {
                rvSearchNews.setPadding(0,0,0,0)
            }

        }
    }

    private fun setUpRecyclerView(view:View) {
        newsAdaptor = NewsAdaptor()
        rvSearchNews.adapter = newsAdaptor
        rvSearchNews.layoutManager  = LinearLayoutManager(activity)
        rvSearchNews.addOnScrollListener(this@SearchNewsFragment.scrollListener)
    }
}