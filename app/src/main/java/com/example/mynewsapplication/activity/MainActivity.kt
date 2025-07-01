package com.example.mynewsapplication.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mynewsapplication.utils.ButtonText
import com.example.mynewsapplication.utils.ErrorString
import com.example.mynewsapplication.utils.Keys
import com.example.mynewsapplication.utils.showToast
import com.example.mynewsapplication.callback.NewsCallback
import com.example.mynewsapplication.databinding.ActivityMainBinding
import com.example.mynewsapplication.recyclerview.newsRecyclerview.NewsAdapter
import com.example.mynewsapplication.recyclerview.shimmerRecyclerview.ShimmerAdapter
import com.example.mynewsapplication.viewModel.NewsViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() , NewsCallback {
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]
        viewModel.newsCallback = this
        recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        observeNewsList()
        setListener()

    }


    private fun setUpShimmerLoading() {
        binding.shimmerRecyclerview.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.shimmerRecyclerview.adapter = ShimmerAdapter()
        binding.swipeRefreshLayout.visibility = View.GONE
        binding.shimmerView.visibility = View.VISIBLE
        binding.shimmerView.startShimmer()
    }

    private fun stopShimmer() {
        lifecycleScope.launch {
            binding.shimmerView.stopShimmer()
            binding.shimmerView.visibility = View.GONE
            binding.swipeRefreshLayout.visibility = View.VISIBLE
        }
    }

    private fun loadOnlineList() {
        binding.swipeRefreshLayout.isEnabled = true
        binding.noDataLayout.visibility = View.GONE
        setUpShimmerLoading()
        viewModel.callNewsApi()
    }
    private fun loadOfflineList() {
        binding.shimmerView.visibility = View.GONE
        binding.noDataLayout.visibility = View.GONE
        binding.swipeRefreshLayout.isEnabled = false
        viewModel.loadOfflineList()
    }

    private fun observeNewsList() {
        if (viewModel.newsList.isEmpty()) {
            loadOnlineList()
        }
        viewModel.newsLiveList.observe(this) { list ->
            stopShimmer()
            recyclerView.adapter = NewsAdapter(list) { clickedArticle ->
                showToast(clickedArticle.title ?: "")
                val intent = Intent(this, ShowDetailedArticleActivity::class.java)
                intent.putExtra(Keys.clickedArticleKey, clickedArticle)
                startActivity(intent)
            }
        }

        viewModel.offlineNewsLiveList.observe(this) {offlineList ->
            when  {
                offlineList.isEmpty() -> noSaveArticle()
                else -> viewModel.updateNewsList(offlineList)
            }
        }
        viewModel.isOfflineMode.observe(this) { isOfflineMode ->
            when (isOfflineMode) {
                true -> loadOfflineList()
                else -> loadOnlineList()
            }
            binding.switchOffline.isChecked = isOfflineMode
        }

        viewModel.searchBarText.observe(this){ searchText ->
            viewModel.filterNewsList(searchText)
        }
    }
    private fun setListener() {
        binding.searchTextinputedittext.addTextChangedListener { text ->
            viewModel.searchBarText.value = text.toString()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadOnlineList()
        }

        binding.switchOffline.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isOfflineMode.value = isChecked
        }
        binding.retryButton.setOnClickListener {
            binding.switchOffline.isChecked = false
            loadOnlineList()
        }

    }

    override fun onApiSuccess() {
        binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onApiFail(error: String) {
        lifecycleScope.launch {
            showNoDataLayout(error, ButtonText.onApiFail)
        }

    }

    override fun noSaveArticle() {
        lifecycleScope.launch {
            showNoDataLayout(ErrorString.noSavedArticle, ButtonText.noSaveArticle)
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isOfflineMode.value == true) {
            viewModel.loadOfflineList()
        }
    }

    private fun showNoDataLayout(error : String, buttonText : String) {
        binding.swipeRefreshLayout.visibility = View.GONE
        binding.shimmerView.visibility = View.GONE
        binding.errorMsgTextview.text = error
        binding.retryButton.text = buttonText
        binding.noDataLayout.visibility = View.VISIBLE
    }
}