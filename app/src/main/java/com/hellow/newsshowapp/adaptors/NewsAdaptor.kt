package com.hellow.newsshowapp.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellow.newsshowapp.R
import com.hellow.newsshowapp.models.Article

class NewsAdaptor: RecyclerView.Adapter<NewsAdaptor.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)



    // for faster change in view
    private val differCallBack = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
              return oldItem.url == newItem.url
         }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
              return oldItem == newItem
         }

    }


    val differ = AsyncListDiffer(this,differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {

       return ArticleViewHolder(LayoutInflater.from(parent.context).inflate(
           R.layout.item_article_preview,parent,false
       ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
         val currentItem =  differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this).load(currentItem.urlToImage).into(findViewById(R.id.ivArticleImage))
            val tvSource: TextView = findViewById(R.id.tvSource)
            val tvTitle: TextView = findViewById(R.id.tvTitle)
            val tvDescription: TextView = findViewById(R.id.tvDescription)
            val tvPublishedAt: TextView = findViewById(R.id.tvPublishedAt)
            tvSource.text = currentItem.source?.name ?: "source"
            tvTitle.text = currentItem.title
            tvDescription.text = currentItem.description
            tvPublishedAt.text = currentItem.publishedAt
            setOnClickListener {
                onItemClickListener?.let { it(currentItem) }
            }
        }

    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}