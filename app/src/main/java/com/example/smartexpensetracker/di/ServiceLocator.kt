package com.example.smartexpensetracker.di

import android.content.Context
import androidx.room.Room
import com.example.smartexpensetracker.data.ExpenseRepository
import com.example.smartexpensetracker.data.room.ExpenseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking

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

	fun preloadBlocking() {
		// Load existing expenses synchronously before UI
		runBlocking(Dispatchers.IO) {
			val initial = database.dao().getAllOnce().map { it.toDomain() }
			repository.replaceAll(initial)
		}
	}
}