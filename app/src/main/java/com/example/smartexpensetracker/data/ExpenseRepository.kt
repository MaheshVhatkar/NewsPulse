package com.example.smartexpensetracker.data

import com.example.smartexpensetracker.data.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class ExpenseRepository {
	private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
	val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

	fun addExpense(expense: Expense) {
		_expenses.update { current -> current + expense }
	}

	fun getExpensesForDate(date: LocalDate): List<Expense> =
		_expenses.value.filter { it.date() == date }

	fun getAll(): List<Expense> = _expenses.value
}