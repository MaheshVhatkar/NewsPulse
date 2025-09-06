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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
		DateAmountGraph(dates = last7, amounts = last7.map { totalsByDay[it] ?: 0.0 })
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
private fun DateAmountGraph(dates: List<LocalDate>, amounts: List<Double>) {
	val max = (amounts.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
	val min = 0.0
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

		// Y-axis ticks (0, 50%, 100%)
		for (i in 0..2) {
			val y = topPadding + height - (i / 2f) * height
			drawLine(Color.LightGray, start = androidx.compose.ui.geometry.Offset(leftPadding, y), end = androidx.compose.ui.geometry.Offset(leftPadding + width, y))
		}

		// Plot line
		val stepX = if (amounts.size > 1) width / (amounts.size - 1) else 0f
		val path = Path()
		amounts.forEachIndexed { index, value ->
			val x = leftPadding + index * stepX
			val y = topPadding + height - ((value - min) / (max - min)).toFloat() * height
			if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
		}
		drawPath(path = path, color = Color(0xFF2196F3), style = Stroke(width = 4f))

		// Points
		amounts.forEachIndexed { index, value ->
			val x = leftPadding + index * stepX
			val y = topPadding + height - ((value - min) / (max - min)).toFloat() * height
			drawCircle(color = Color(0xFF1976D2), radius = 6f, center = androidx.compose.ui.geometry.Offset(x, y))
		}

		// X-axis labels (dates)
		dates.forEachIndexed { index, date ->
			val x = leftPadding + index * stepX
			drawContext.canvas.nativeCanvas.apply {
				drawText(
					date.format(formatter),
					x,
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

