package com.example.mynewsapplication.recyclerview.shimmerRecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mynewsapplication.databinding.NewsItemShimmerBinding

class ShimmerAdapter(private val itemCount : Int = 10) : Adapter<ShimmerAdapter.ShimmerViewHolder>(){

    class ShimmerViewHolder(binding : NewsItemShimmerBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShimmerViewHolder {
        val view = NewsItemShimmerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShimmerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemCount
    }

    override fun onBindViewHolder(holder: ShimmerViewHolder, position: Int) {
        //No need to bind.
    }


}