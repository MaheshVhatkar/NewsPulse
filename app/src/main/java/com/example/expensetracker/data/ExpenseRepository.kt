package com.example.expensetracker.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

interface ExpenseRepository {
	val expenses: Flow<List<Expense>>
	suspend fun add(expense: Expense)
	suspend fun remove(id: String)
	fun totalsByDate(lastDays: Long = 7): Flow<Map<LocalDate, Double>>
	fun totalsByCategory(lastDays: Long = 7): Flow<Map<ExpenseCategory, Double>>
}

class InMemoryExpenseRepository : ExpenseRepository {
	private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
	override val expenses: StateFlow<List<Expense>> = _expenses

	override suspend fun add(expense: Expense) {
		_expenses.value = _expenses.value + expense
	}

	override suspend fun remove(id: String) {
		_expenses.value = _expenses.value.filterNot { it.id == id }
	}

	override fun totalsByDate(lastDays: Long): StateFlow<Map<LocalDate, Double>> {
		return _expenses.map { list ->
			list.groupBy { it.localDate() }
				.mapValues { (_, v) -> v.sumOf { it.amountInRupees } }
		}.asStateFlow()
	}

	override fun totalsByCategory(lastDays: Long): StateFlow<Map<ExpenseCategory, Double>> {
		return _expenses.map { list ->
			list.groupBy { it.category }
				.mapValues { (_, v) -> v.sumOf { it.amountInRupees } }
		}.asStateFlow()
	}
}

// Helper to convert Flow to hot StateFlow without extra scope here
private fun <T> kotlinx.coroutines.flow.Flow<T>.asStateFlow(): MutableStateFlow<T> {
	val initial = when (this) {
		is StateFlow<T> -> this.value
		else -> null
	}
	val state = MutableStateFlow(initial as T)
	kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Default) {
		this@asStateFlow.collect { state.value = it }
	}
	return state
}

