package com.example.expensetracker.ui.screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.SmartExpenseApp
import com.example.expensetracker.ui.vm.ExpenseViewModelFactory
import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.ExpenseCategory
import com.example.expensetracker.ui.vm.ExpenseViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(padding: PaddingValues, vm: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory((androidx.compose.ui.platform.LocalContext.current.applicationContext as SmartExpenseApp).repository))) {
	var date by remember { mutableStateOf(LocalDate.now()) }
	var groupByCategory by remember { mutableStateOf(true) }
	var pendingDelete by remember { mutableStateOf<Expense?>(null) }

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
			Spacer(Modifier.height(4.dp))
			TabRow(selectedTabIndex = if (groupByCategory) 0 else 1) {
				Tab(selected = groupByCategory, onClick = { groupByCategory = true }, text = { Text("By Category") })
				Tab(selected = !groupByCategory, onClick = { groupByCategory = false }, text = { Text("By Time") })
			}
			Spacer(Modifier.height(4.dp))
			Text("Total: count=${expenses.size}, amount=₹%.2f".format(expenses.sumOf { it.amountInRupees }))
		}
		Spacer(Modifier.height(4.dp))
		if (expenses.isEmpty()) {
			Text("No expenses for this date")
		} else {
			if (groupByCategory) {
				val groups = expenses.groupBy { it.category }
				LazyColumn(contentPadding = padding) {
					groups.forEach { (cat, list) ->
						item { Text(cat.name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
						items(list) { ExpenseCard(it) { pendingDelete = it } }
					}
				}
			} else {
				LazyColumn(contentPadding = padding) {
					items(expenses.sortedBy { it.epochMillis }) { ExpenseCard(it) { pendingDelete = it } }
				}
			}
		}

		if (pendingDelete != null) {
			AlertDialog(
				onDismissRequest = { pendingDelete = null },
				title = { Text("Remove expense?") },
				text = { Text(pendingDelete!!.title) },
				confirmButton = {
					TextButton(onClick = {
						vm.removeExpense(pendingDelete!!.id)
						pendingDelete = null
					}) { Text("Remove") }
				},
				dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Cancel") } }
			)
		}
	}
}

@Composable
private fun ExpenseCard(expense: Expense, onLongPressDelete: (Expense) -> Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp)
			.combinedClickable(
				onClick = {},
				onLongClick = { onLongPressDelete(expense) }
			),
		colors = CardDefaults.cardColors()
	) {
		Column(Modifier.padding(16.dp)) {
			Row(Modifier.fillMaxWidth()) {
				Text(expense.title, fontWeight = FontWeight.SemiBold)
				Spacer(Modifier.weight(1f))
				Text("₹${"%.2f".format(expense.amountInRupees)}")
			}
			Spacer(Modifier.height(4.dp))
			val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
			val dateTime = java.time.Instant.ofEpochMilli(expense.epochMillis).atZone(ZoneId.systemDefault()).toLocalDateTime()
			Text("${expense.category} • ${dateTime.format(formatter)}")
			if (!expense.notes.isNullOrBlank()) {
				Spacer(Modifier.height(4.dp))
				Text(expense.notes!!)
			}
		}
	}
}

