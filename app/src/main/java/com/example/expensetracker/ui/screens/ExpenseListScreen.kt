package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.SmartExpenseApp
import com.example.expensetracker.ui.vm.ExpenseViewModelFactory
import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.ExpenseCategory
import com.example.expensetracker.ui.vm.ExpenseViewModel
import java.time.LocalDate
import com.example.expensetracker.data.asLocalDateTimeString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(padding: PaddingValues, vm: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory((androidx.compose.ui.platform.LocalContext.current.applicationContext as SmartExpenseApp).repository))) {
	var date by remember { mutableStateOf(LocalDate.now()) }
	var groupByCategory by remember { mutableStateOf(true) }

	val expenses = vm.expenses.collectAsState().value.filter { it.localDate() == date }

	Column(
		modifier = Modifier
			.fillMaxSize(),
		verticalArrangement = Arrangement.Top
	) {
		Column(Modifier.fillMaxWidth()) {
			Text("Selected: $date")
			Spacer(Modifier.height(8.dp))
			AssistChip(
				onClick = { date = date.minusDays(1) },
				label = { Text("Prev Day") }
			)
			Spacer(Modifier.height(8.dp))
			AssistChip(
				onClick = { date = date.plusDays(1) },
				label = { Text("Next Day") },
				colors = AssistChipDefaults.assistChipColors()
			)
			Spacer(Modifier.height(8.dp))
			FilterChip(selected = groupByCategory, onClick = { groupByCategory = !groupByCategory }, label = {
				Text(if (groupByCategory) "Group: Category" else "Group: Time")
			})
			Spacer(Modifier.height(8.dp))
			Text("Total: count=${expenses.size}, amount=₹%.2f".format(expenses.sumOf { it.amountInRupees }))
		}
		Spacer(Modifier.height(12.dp))
		if (expenses.isEmpty()) {
			Text("No expenses for this date")
		} else {
			if (groupByCategory) {
				val groups = expenses.groupBy { it.category }
				LazyColumn(contentPadding = padding) {
					groups.forEach { (cat, list) ->
						item { Text(cat.name) }
						items(list) { ExpenseRow(it) }
					}
				}
			} else {
				LazyColumn(contentPadding = padding) {
					items(expenses.sortedBy { it.epochMillis }) { ExpenseRow(it) }
				}
			}
		}
	}
}

@Composable
private fun ExpenseRow(expense: Expense) {
	val base = "${expense.title} - ₹${"%.2f".format(expense.amountInRupees)} - ${expense.category} - ${expense.epochMillis.asLocalDateTimeString()}"
	val notes = expense.notes?.trim().orEmpty()
	Text(if (notes.isNotEmpty()) "$base\nNotes: $notes" else base)
}

