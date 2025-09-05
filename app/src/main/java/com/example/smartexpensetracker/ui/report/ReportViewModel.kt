package com.example.smartexpensetracker.ui.report

import androidx.lifecycle.ViewModel
import com.example.smartexpensetracker.data.ExpenseRepository
import com.example.smartexpensetracker.data.model.Expense
import com.example.smartexpensetracker.data.model.ExpenseCategory
import java.time.LocalDate

class ReportViewModel(private val repository: ExpenseRepository) : ViewModel() {
	data class DailyTotal(val date: LocalDate, val total: Double)

	fun last7DaysTotals(): List<DailyTotal> {
		val today = LocalDate.now()
		return (0..6).map { offset ->
			val d = today.minusDays(offset.toLong())
			DailyTotal(d, repository.getExpensesForDate(d).sumOf { it.amountInRupees })
		}.reversed()
	}

	fun categoryTotalsLast7Days(): Map<ExpenseCategory, Double> {
		val today = LocalDate.now()
		val last7 = (0..6).map { today.minusDays(it.toLong()) }.toSet()
		return repository.getAll()
			.filter { it.date() in last7 }
			.groupBy { it.category }
			.mapValues { (_, list) -> list.sumOf { it.amountInRupees } }
	}
}