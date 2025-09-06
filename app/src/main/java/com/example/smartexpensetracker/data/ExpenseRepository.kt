package com.example.smartexpensetracker.data

import com.example.smartexpensetracker.data.model.Expense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ExpenseRepository(
	private val roomDao: com.example.smartexpensetracker.data.room.ExpenseDao? = null,
	private val appScope: CoroutineScope? = null
) {
	private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
	val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

	init {
		if (roomDao != null && appScope != null) {
			appScope.launch(Dispatchers.IO) {
				val initial = roomDao.getAllOnce().map { it.toDomain() }
				replaceAll(initial)
			}
		}
	}

	fun replaceAll(newList: List<Expense>) {
		_expenses.value = newList
	}

	fun addExpense(expense: Expense) {
		_expenses.update { current -> current + expense }
		// Persist to Room if available
		if (roomDao != null && appScope != null) {
			appScope.launch(Dispatchers.IO) {
				roomDao.upsert(expense.toEntity())
			}
		}
	}

	fun getExpensesForDate(date: LocalDate): List<Expense> =
		_expenses.value.filter { it.date() == date }

	fun getAll(): List<Expense> = _expenses.value
}