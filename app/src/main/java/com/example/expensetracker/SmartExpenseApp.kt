package com.example.expensetracker

import android.app.Application
import androidx.room.Room
import com.example.expensetracker.data.ExpenseDatabase
import com.example.expensetracker.data.RoomExpenseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class SmartExpenseApp : Application() {
	private val applicationScope = CoroutineScope(SupervisorJob())

	val database: ExpenseDatabase by lazy {
		Room.databaseBuilder(this, ExpenseDatabase::class.java, "expenses.db").build()
	}

	val repository by lazy { RoomExpenseRepository(database.dao(), applicationScope) }
}

