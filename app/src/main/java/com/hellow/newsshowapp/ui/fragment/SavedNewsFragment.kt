package com.hellow.newsshowapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hellow.newsshowapp.R
import com.hellow.newsshowapp.adaptors.NewsAdaptor
import com.hellow.newsshowapp.ui.NewsActivity
import com.hellow.newsshowapp.ui.NewsViewModel

class SavedNewsFragment: Fragment( R.layout.fragment_saved_news) {


lateinit var viewModel: NewsViewModel
lateinit var newsAdaptor: NewsAdaptor

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel = (activity as NewsActivity).viewModel
    val rvSavedNews: RecyclerView = view.findViewById(R.id.rvSavedNews)
    setUpRecyclerView(view,rvSavedNews)

    newsAdaptor.setOnItemClickListener { article ->
        val bundle = Bundle().apply {
            putSerializable("article",article)
        }

        findNavController().navigate(
            R.id.action_savedNewsFragment_to_articleFragment,bundle
        )
    }

    val itemTouchHelperCallBack  = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN , ItemTouchHelper.LEFT or
        ItemTouchHelper.RIGHT
    ){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
             return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
              val position = viewHolder.adapterPosition
            val article = newsAdaptor.differ.currentList[position]
            viewModel.deleteArticle(article)
            Snackbar.make(view,"deleted forever",Snackbar.LENGTH_LONG).apply {
                setAction("StopDelete"){
                    viewModel.saveArticle(article)
                }
                show()
            }
        }

    }

    ItemTouchHelper(itemTouchHelperCallBack).apply {
        attachToRecyclerView(rvSavedNews)
    }

    viewModel.getSavedArticle().observe(viewLifecycleOwner, Observer {
        newsAdaptor.differ.submitList(it)
    })

}

    private fun setUpRecyclerView(view:View,rvSavedNews:RecyclerView) {
        newsAdaptor = NewsAdaptor()

        rvSavedNews.adapter = newsAdaptor
        rvSavedNews.layoutManager  = LinearLayoutManager(activity)
    }


}