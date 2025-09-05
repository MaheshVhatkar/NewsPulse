package com.example.smartexpensetracker.ui.list

import androidx.lifecycle.ViewModel
import com.example.smartexpensetracker.data.ExpenseRepository
import com.example.smartexpensetracker.data.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class ListViewModel(private val repository: ExpenseRepository) : ViewModel() {
	private val _date = MutableStateFlow(LocalDate.now())
	val date: StateFlow<LocalDate> = _date.asStateFlow()

	private val _groupByCategory = MutableStateFlow(true)
	val groupByCategory: StateFlow<Boolean> = _groupByCategory.asStateFlow()

	fun setDate(date: LocalDate) { _date.value = date }
	fun toggleGroup() { _groupByCategory.value = !_groupByCategory.value }

	fun expensesForSelectedDate(): List<Expense> = repository.getExpensesForDate(_date.value)
}