package com.example.expensetracker.data

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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

private val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

fun Long.asLocalDateTimeString(): String =
	Instant.ofEpochMilli(this)
		.atZone(ZoneId.systemDefault())
		.toLocalDateTime()
		.format(DATE_TIME_FORMATTER)

