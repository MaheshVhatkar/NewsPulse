package com.example.expensetracker.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class RoomExpenseRepository(
	private val dao: ExpenseDao,
	private val appScope: CoroutineScope
) : ExpenseRepository {
	override val expenses: Flow<List<Expense>> =
		dao.observeAll().map { it.map { e -> e.toDomain() } }

	override suspend fun add(expense: Expense) {
		dao.upsert(expense.toEntity())
	}

	override suspend fun remove(id: String) {
		dao.deleteById(id)
	}

	override fun totalsByDate(lastDays: Long): Flow<Map<LocalDate, Double>> {
		return expenses.map { list ->
			list.groupBy { it.localDate() }
				.mapValues { (_, v) -> v.sumOf { it.amountInRupees } }
		}
	}

	override fun totalsByCategory(lastDays: Long): Flow<Map<ExpenseCategory, Double>> {
		return expenses.map { list ->
			list.groupBy { it.category }
				.mapValues { (_, v) -> v.sumOf { it.amountInRupees } }
		}
	}
}

