package com.example.smartexpensetracker.data.model

import java.time.LocalDate
import java.time.LocalDateTime

enum class ExpenseCategory { Staff, Travel, Food, Utility }

data class Expense(
	val id: String,
	val title: String,
	val amountInRupees: Double,
	val category: ExpenseCategory,
	val notes: String?,
	val dateTime: LocalDateTime,
	val receiptImageUri: String? = null
) {
	fun date(): LocalDate = dateTime.toLocalDate()
}