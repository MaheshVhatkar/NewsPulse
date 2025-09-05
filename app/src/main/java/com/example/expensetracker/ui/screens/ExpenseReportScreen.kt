package com.example.expensetracker.ui.screens

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.SmartExpenseApp
import com.example.expensetracker.ui.vm.ExpenseViewModelFactory
import com.example.expensetracker.data.ExpenseCategory
import com.example.expensetracker.ui.vm.ExpenseViewModel
import java.time.LocalDate

@Composable
fun ExpenseReportScreen(padding: PaddingValues, vm: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory((androidx.compose.ui.platform.LocalContext.current.applicationContext as SmartExpenseApp).repository))) {
	val context = LocalContext.current
	val all = vm.expenses.collectAsState().value
	val last7 = (0..6).map { LocalDate.now().minusDays((6 - it).toLong()) }
	val totalsByDay = last7.associateWith { date ->
		all.filter { it.localDate() == date }.sumOf { it.amountInRupees }
	}
	val totalsByCategory = ExpenseCategory.entries.associateWith { cat ->
		all.filter { it.category == cat }.sumOf { it.amountInRupees }
	}

	Column(Modifier.padding(padding).padding(16.dp)) {
		Text("Last 7 Days Totals")
		BarChart(values = totalsByDay.values.toList())
		Text("Category Totals")
		BarChart(values = totalsByCategory.values.toList(), barColor = Color(0xFF4CAF50))
		Button(onClick = {
			val csv = buildString {
				appendLine("Date,Total")
				last7.forEach { d -> appendLine("$d,${totalsByDay[d] ?: 0.0}") }
			}
			val intent = Intent(Intent.ACTION_SEND).apply {
				type = "text/csv"
				putExtra(Intent.EXTRA_TEXT, csv)
			}
			context.startActivity(Intent.createChooser(intent, "Export CSV"))
		}) { Text("Export CSV (mock)") }
	}
}

@Composable
private fun BarChart(values: List<Double>, barColor: Color = Color(0xFF2196F3)) {
	val max = (values.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
	Canvas(Modifier.fillMaxWidth().height(120.dp)) {
		val barWidth = size.width / (values.size * 2f)
		values.forEachIndexed { index, v ->
			val height = (v / max).toFloat() * size.height
			drawRect(
				color = barColor,
				topLeft = androidx.compose.ui.geometry.Offset(x = (index * 2 + 1) * barWidth, y = size.height - height),
				size = androidx.compose.ui.geometry.Size(width = barWidth, height = height)
			)
		}
	}
}

