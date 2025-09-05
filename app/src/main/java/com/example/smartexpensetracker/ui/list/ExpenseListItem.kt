package com.example.smartexpensetracker.ui.list

import com.example.smartexpensetracker.data.model.Expense

sealed class ExpenseListItem {
	data class Header(val title: String) : ExpenseListItem()
	data class Row(val expense: Expense) : ExpenseListItem()
}