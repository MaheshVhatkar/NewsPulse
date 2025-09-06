package com.example.smartexpensetracker.di

import android.content.Context
import androidx.room.Room
import com.example.smartexpensetracker.data.ExpenseRepository
import com.example.smartexpensetracker.data.room.ExpenseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

object ServiceLocator {
	private lateinit var appContext: Context
	private val applicationScope = CoroutineScope(SupervisorJob())

	fun init(context: Context) {
		appContext = context.applicationContext
	}

	private val database: ExpenseDatabase by lazy {
		Room.databaseBuilder(appContext, ExpenseDatabase::class.java, "expenses.db").build()
	}

	val repository: ExpenseRepository by lazy { ExpenseRepository(database.dao(), applicationScope) }
}