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
import com.example.expensetracker.ui.vm.ExpenseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ExpenseReportScreen(padding: PaddingValues, vm: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory((androidx.compose.ui.platform.LocalContext.current.applicationContext as SmartExpenseApp).repository))) {
	val context = LocalContext.current
	val all = vm.expenses.collectAsState().value
	val last7 = (0..6).map { LocalDate.now().minusDays((6 - it).toLong()) }
	val totalsByDay = last7.associateWith { date ->
		all.filter { it.localDate() == date }.sumOf { it.amountInRupees }
	}

	Column(Modifier.padding(padding).padding(16.dp)) {
		Text("Spending (Last 7 Days)")
		DateAmountBarGraph(dates = last7, amounts = last7.map { totalsByDay[it] ?: 0.0 })
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
private fun DateAmountBarGraph(dates: List<LocalDate>, amounts: List<Double>) {
	val max = (amounts.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
	val formatter = DateTimeFormatter.ofPattern("dd/MM")

	Canvas(Modifier.fillMaxWidth().height(220.dp)) {
		val leftPadding = 40f
		val bottomPadding = 30f
		val topPadding = 10f
		val rightPadding = 10f

		val width = size.width - leftPadding - rightPadding
		val height = size.height - topPadding - bottomPadding

		// Axes
		drawLine(Color.Gray, start = androidx.compose.ui.geometry.Offset(leftPadding, topPadding), end = androidx.compose.ui.geometry.Offset(leftPadding, topPadding + height))
		drawLine(Color.Gray, start = androidx.compose.ui.geometry.Offset(leftPadding, topPadding + height), end = androidx.compose.ui.geometry.Offset(leftPadding + width, topPadding + height))

		// Bars
		if (amounts.isNotEmpty()) {
			val barSpace = width / (amounts.size * 2f)
			amounts.forEachIndexed { index, value ->
				val barLeft = leftPadding + (index * 2 + 0.5f) * barSpace
				val barRight = barLeft + barSpace
				val barHeight = ((value / max).toFloat()) * height
				val top = topPadding + height - barHeight
				drawRect(
					color = Color(0xFF2196F3),
					topLeft = androidx.compose.ui.geometry.Offset(barLeft, top),
					size = androidx.compose.ui.geometry.Size(barRight - barLeft, barHeight)
				)

				// X labels
				drawContext.canvas.nativeCanvas.apply {
					drawText(
						dates[index].format(formatter),
						(barLeft + barRight) / 2f,
						topPadding + height + 20f,
						android.graphics.Paint().apply {
							textAlign = android.graphics.Paint.Align.CENTER
							textSize = 28f
							color = android.graphics.Color.GRAY
						}
					)
				}
			}
		}
	}
}

