package com.example.mynewsapplication.recyclerview.newsRecyclerview

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.mynewsapplication.apiCalll.Article
import com.example.mynewsapplication.databinding.NewsItemBinding

class NewsViewHolder(private val binding: NewsItemBinding) : ViewHolder(binding.root) {

    fun bind(item : Article, onClick : (Article) -> Unit) {

        binding.titleTextview.text = item.title
        binding.descriptionTextview.text = item.description
        Glide.with(binding.root.context).apply {
            load(item.urlToImage).into(binding.newsImageview)
        }

        binding.newsCardview.setOnClickListener {
            onClick(item)
        }

    }
}