package com.example.smartexpensetracker.ui.entry

import androidx.lifecycle.ViewModel
import com.example.smartexpensetracker.data.ExpenseRepository
import com.example.smartexpensetracker.data.model.Expense
import com.example.smartexpensetracker.data.model.ExpenseCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class EntryViewModel(private val repository: ExpenseRepository) : ViewModel() {
	private val _todayTotal = MutableStateFlow(0.0)
	val todayTotal: StateFlow<Double> = _todayTotal.asStateFlow()

	init {
		refreshTodayTotal()
	}

	fun refreshTodayTotal() {
		val total = repository.getExpensesForDate(LocalDate.now()).sumOf { it.amountInRupees }
		_todayTotal.value = total
	}

	fun addExpense(
		title: String,
		amountRupees: String,
		category: ExpenseCategory,
		notes: String?,
		receiptImageUri: String?
	): Result<Unit> {
		if (title.isBlank()) return Result.failure(IllegalArgumentException("Title required"))
		val amount = amountRupees.toDoubleOrNull()
			?: return Result.failure(IllegalArgumentException("Invalid amount"))
		if (amount <= 0.0) return Result.failure(IllegalArgumentException("Amount must be > 0"))
		if (notes != null && notes.length > 100) return Result.failure(IllegalArgumentException("Notes too long"))

		val now = LocalDateTime.now()

		// Simple duplicate check: same title & amount within last 15 minutes
		val fifteenMinutes = Duration.ofMinutes(15)
		val isDuplicate = repository.getAll().any { existing ->
			existing.title.equals(title.trim(), ignoreCase = true) &&
			existing.amountInRupees == amount &&
			Duration.between(existing.dateTime, now).abs() <= fifteenMinutes
		}
		if (isDuplicate) {
			return Result.failure(IllegalStateException("Possible duplicate expense"))
		}

		val expense = Expense(
			id = UUID.randomUUID().toString(),
			title = title.trim(),
			amountInRupees = amount,
			category = category,
			notes = notes?.trim(),
			dateTime = now,
			receiptImageUri = receiptImageUri
		)
		repository.addExpense(expense)
		refreshTodayTotal()
		return Result.success(Unit)
	}
}