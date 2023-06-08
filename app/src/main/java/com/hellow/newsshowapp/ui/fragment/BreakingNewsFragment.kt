package com.hellow.newsshowapp.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
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
import com.hellow.newsshowapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.hellow.newsshowapp.utils.Resource


class BreakingNewsFragment: Fragment( R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdaptor: NewsAdaptor
    lateinit var rvBreakingNews: RecyclerView
    lateinit var paginationProgressBar:ProgressBar

    val TAG = "BreakingNewsFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel

         paginationProgressBar= view.findViewById(R.id.paginationProgressBar)
         rvBreakingNews= view.findViewById(R.id.rvBreakingNews)
        setUpRecyclerView(view)

        newsAdaptor.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article",article)
            }

             findNavController().navigate(
          R.id.action_breakingNewsFragment_to_articleFragment,bundle
             )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar(paginationProgressBar)
                    if (response.data != null) {
                        newsAdaptor.differ.submitList(response.data.articles.toList())
                        val totalPages = response.data.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if(isLastPage) {
                            rvBreakingNews.setPadding(0, 0, 0, 0)
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

    private fun showSnackBar(message:String,view: View){
        val snackBar = Snackbar
            .make(view,
                    "$message", Snackbar.LENGTH_LONG)
        snackBar.setTextColor(Color.RED)
        snackBar.show()

    }

    private fun hideProgressBar(paginationProgressBar:ProgressBar) {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(paginationProgressBar:ProgressBar) {
        paginationProgressBar.visibility = View.VISIBLE
       isLoading = true

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
            val isTotalMoreThenVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThenVisible && isScrolling
            if(shouldPaginate)
            {
                viewModel.getBreakingNews("us")
                isScrolling = false
            }

         }
    }

    private fun setUpRecyclerView(view:View) {
        newsAdaptor = NewsAdaptor()
        rvBreakingNews.adapter = newsAdaptor
        rvBreakingNews.layoutManager  = LinearLayoutManager(activity)
        rvBreakingNews.addOnScrollListener(this@BreakingNewsFragment.scrollListener)
    }


}