package com.example.mynewsapplication.recyclerview.newsRecyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.mynewsapplication.apiCalll.Article
import com.example.mynewsapplication.databinding.NewsItemBinding

class NewsAdapter(private val list : List<Article>,
    private val onClick : (Article) -> Unit)  : Adapter<NewsViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = NewsItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(list[position], onClick)
    }
}