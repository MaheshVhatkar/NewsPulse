package com.example.mynewsapplication.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mynewsapplication.R
import com.example.mynewsapplication.utils.EmptyObject
import com.example.mynewsapplication.utils.Keys
import com.example.mynewsapplication.utils.ToastMessages
import com.example.mynewsapplication.utils.showToast
import com.example.mynewsapplication.apiCalll.Article
import com.example.mynewsapplication.callback.ShowArticleCallback
import com.example.mynewsapplication.databinding.ActivityShowDetailedArticleBinding
import com.example.mynewsapplication.viewModel.ShowArticleViewModel

class ShowDetailedArticleActivity : AppCompatActivity() , ShowArticleCallback{
    private lateinit var viewModel : ShowArticleViewModel
    private lateinit var dataBinding : ActivityShowDetailedArticleBinding
    private lateinit var article: Article
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        viewModel = ViewModelProvider(this)[ShowArticleViewModel::class.java]
        dataBinding = ActivityShowDetailedArticleBinding.inflate(layoutInflater)
        dataBinding.lifecycleOwner = this
        setContentView(dataBinding.root)
        dataBinding.viewModel = viewModel

        article = intent.getParcelableExtra(Keys.clickedArticleKey) ?: EmptyObject.emptyArticle
        viewModel.title.value = article.title ?: ""
        viewModel.webLink.value = article.url ?: ""
        viewModel.description.value = article.content ?: ""
        Glide.with(this)
            .load(article?.urlToImage).into(dataBinding.newsImageview)
        setListener()
        viewModel.isArticleAlreadySaved(article)

    }

    override fun webLinkClicked() {
        val url = viewModel.webLink.value
        if (!url.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun setListener() {
        dataBinding.extraOptionsImagebutton.setOnClickListener{
            val popUp = PopupMenu(this,it )
            when (viewModel.isArticleSaved.value) {
                false -> {popUp.menuInflater.inflate(R.menu.save_article, popUp.menu)
                    popUp.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.save_article -> {
                                showToast(ToastMessages.savedArticle)
                                viewModel.saveArticleOffline(article)
                                viewModel.isArticleSaved.value = true
                                true}
                            else -> false
                        }
                    }
                }
                else -> {popUp.menuInflater.inflate(R.menu.remove_article, popUp.menu)
                    popUp.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.remove_article -> {
                                showToast(ToastMessages.removedArticle)
                                viewModel.removeArticleOffline(article)
                                viewModel.isArticleSaved.value = false
                                finish()
                                true}
                            else -> false
                        }
                    }
                }
            }

            popUp.gravity = Gravity.CENTER
            popUp.show()
        }
    }
}