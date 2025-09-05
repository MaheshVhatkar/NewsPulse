package com.example.expensetracker.data

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

enum class ExpenseCategory { Staff, Travel, Food, Utility }

data class Expense(
	val id: String = UUID.randomUUID().toString(),
	val title: String,
	val amountInRupees: Double,
	val category: ExpenseCategory,
	val notes: String?,
	val epochMillis: Long = System.currentTimeMillis(),
	val receiptImageUri: String? = null
) {
	fun localDate(): LocalDate = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate()
}

