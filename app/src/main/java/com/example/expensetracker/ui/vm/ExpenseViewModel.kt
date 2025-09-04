package com.example.expensetracker.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.ExpenseCategory
import com.example.expensetracker.data.ExpenseRepository
import com.example.expensetracker.data.InMemoryExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class FormState(
	val title: String = "",
	val amount: String = "",
	val category: ExpenseCategory = ExpenseCategory.Staff,
	val notes: String = "",
	val receiptUri: String? = null,
	val error: String? = null,
	val addedAnimationTick: Int = 0
)

class ExpenseViewModel(
	private val repository: ExpenseRepository = InMemoryExpenseRepository()
) : ViewModel() {

	private val _form = MutableStateFlow(FormState())
	val form: StateFlow<FormState> = _form.asStateFlow()

	val expenses = repository.expenses

	fun onTitleChange(v: String) { _form.value = _form.value.copy(title = v) }
	fun onAmountChange(v: String) { _form.value = _form.value.copy(amount = v) }
	fun onCategoryChange(v: ExpenseCategory) { _form.value = _form.value.copy(category = v) }
	fun onNotesChange(v: String) { if (v.length <= 100) _form.value = _form.value.copy(notes = v) }
	fun onReceiptChange(uri: String?) { _form.value = _form.value.copy(receiptUri = uri) }

	fun totalSpentOn(date: LocalDate): Double {
		return expenses.value.filter { it.localDate() == date }.sumOf { it.amountInRupees }
	}

	fun addExpense(onSuccess: () -> Unit = {}) {
		val current = _form.value
		val amount = current.amount.toDoubleOrNull()
		if (current.title.isBlank()) {
			_form.value = current.copy(error = "Title cannot be empty")
			return
		}
		if (amount == null || amount <= 0.0) {
			_form.value = current.copy(error = "Amount must be greater than 0")
			return
		}
		// Duplicate detection: same title (case-insensitive, trimmed) and amount on the same date
		val today = java.time.LocalDate.now()
		val normalizedTitle = current.title.trim().lowercase()
		val duplicateExists = expenses.value.any { e ->
			e.localDate() == today && e.title.trim().lowercase() == normalizedTitle &&
				kotlin.math.abs(e.amountInRupees - (amount ?: 0.0)) < 0.005 && e.category == current.category
		}
		if (duplicateExists) {
			_form.value = current.copy(error = "Duplicate expense detected for today")
			return
		}
		viewModelScope.launch {
			repository.add(
				Expense(
					title = current.title.trim(),
					amountInRupees = amount,
					category = current.category,
					notes = current.notes.ifBlank { null },
					receiptImageUri = current.receiptUri
				)
			)
			_form.value = FormState(addedAnimationTick = current.addedAnimationTick + 1)
			onSuccess()
		}
	}
}

