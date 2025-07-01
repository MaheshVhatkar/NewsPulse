package com.example.mynewsapplication.utils

import com.example.mynewsapplication.apiCalll.Article
import com.example.mynewsapplication.apiCalll.Source

object Keys {
    const val clickedArticleKey = "article"

}
object ErrorString {
    const val noSavedArticle = "No Saved Article"
}

object ButtonText {
    const val onApiFail = "Retry"
    const val noSaveArticle = "Go Online"
}
object EmptyObject {
    val emptyArticle = Article(
        Source("", "")
        ,"", "", "","", "", "", "")
}
object ToastMessages {
    const val savedArticle = "Saved Article"
    const val removedArticle = "Removed Article"
}
object ApiString {
    const val baseUrl = "https://newsapi.org/v2/"
    const val endPoint = "top-headlines"
    const val queryCountry = "country"
    const val queryCategory = "category"
    const val queryApiKey = "apiKey"
    const val country = "us"
    const val category = "business"
    const val apiKey = "4830328eea6d48a5a7090018f2819e06"
}
object RoomString {
    const val tableName = "saved_news_table"
    const val dbName = "news_db"
}