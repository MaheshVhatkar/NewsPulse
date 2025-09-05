package com.example.expensetracker

import android.app.Application
import androidx.room.Room
import com.example.expensetracker.data.ExpenseDatabase
import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.ExpenseCategory
import com.example.expensetracker.data.RoomExpenseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class SmartExpenseApp : Application() {
	private val applicationScope = CoroutineScope(SupervisorJob())

	val database: ExpenseDatabase by lazy {
		Room.databaseBuilder(this, ExpenseDatabase::class.java, "expenses.db").build()
	}

	val repository by lazy { RoomExpenseRepository(database.dao(), applicationScope) }

	override fun onCreate() {
		super.onCreate()
		// Seed sample data if empty
		applicationScope.launch {
			val dao = database.dao()
			if (dao.count() == 0L) {
				val today = LocalDate.now()
				fun dateMillis(d: LocalDate) = d.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
				dao.upsert(Expense(
					title = "Lunch",
					amountInRupees = 250.0,
					category = ExpenseCategory.Food,
					notes = "Team lunch",
					receiptImageUri = null,
					epochMillis = dateMillis(today.minusDays(1))
				).toEntity())
				dao.upsert(Expense(
					title = "Cab",
					amountInRupees = 540.0,
					category = ExpenseCategory.Travel,
					notes = null,
					receiptImageUri = null,
					epochMillis = dateMillis(today.minusDays(2))
				).toEntity())
				dao.upsert(Expense(
					title = "Electricity",
					amountInRupees = 1200.0,
					category = ExpenseCategory.Utility,
					notes = "Office bill",
					receiptImageUri = null,
					epochMillis = dateMillis(today.minusDays(3))
				).toEntity())
				dao.upsert(Expense(
					title = "Wages",
					amountInRupees = 2000.0,
					category = ExpenseCategory.Staff,
					notes = null,
					receiptImageUri = null,
					epochMillis = dateMillis(today.minusDays(4))
				).toEntity())
				dao.upsert(Expense(
					title = "Snacks",
					amountInRupees = 180.0,
					category = ExpenseCategory.Food,
					notes = null,
					receiptImageUri = null,
					epochMillis = dateMillis(today)
				).toEntity())
			}
		}
	}
}

