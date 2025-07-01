package com.example.mynewsapplication.callback

interface NewsCallback {

    fun onApiSuccess()
    fun onApiFail(error : String)
    fun noSaveArticle()
}