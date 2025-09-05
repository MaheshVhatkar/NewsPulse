package com.example.expensetracker.ui.screens

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.SmartExpenseApp
import com.example.expensetracker.ui.vm.ExpenseViewModelFactory
import com.example.expensetracker.ui.vm.ExpenseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.io.File
import androidx.core.content.FileProvider

@Composable
fun ExpenseReportScreen(padding: PaddingValues, vm: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory((androidx.compose.ui.platform.LocalContext.current.applicationContext as SmartExpenseApp).repository))) {
	val context = LocalContext.current
	val all = vm.expenses.collectAsState().value
	val last7 = (0..6).map { LocalDate.now().minusDays((6 - it).toLong()) }
	val totalsByDay = last7.associateWith { date ->
		all.filter { it.localDate() == date }.sumOf { it.amountInRupees }
	}
	val maxDailyAllTime = all
		.groupBy { it.localDate() }
		.map { (_, list) -> list.sumOf { it.amountInRupees } }
		.maxOrNull()
		?.coerceAtLeast(1.0) ?: 1.0

	Column(Modifier.padding(padding).padding(16.dp)) {
		Text("Last 7 Days (Bar)")
		AxisBarChart(
			labels = last7.map { it.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) },
			values = totalsByDay.values.toList(),
			barColor = Color(0xFF2196F3),
			maxY = maxDailyAllTime
		)
		Text("Last 7 Days (Line)")
		AxisLineChart(
			labels = last7.map { it.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) },
			values = totalsByDay.values.toList(),
			lineColor = Color(0xFF4CAF50),
			maxY = maxDailyAllTime
		)
		Button(onClick = {
			val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
			val csv = buildString {
				appendLine("amount,date,category")
				all.forEach { e ->
					appendLine("${e.amountInRupees},${e.localDate().format(formatter)},${e.category}")
				}
			}
			val dir = File(context.cacheDir, "exports").apply { mkdirs() }
			val file = File(dir, "expenses.csv")
			file.writeText(csv)
			val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
			val intent = Intent(Intent.ACTION_SEND).apply {
				type = "text/csv"
				putExtra(Intent.EXTRA_STREAM, uri)
				addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
			}
			context.startActivity(Intent.createChooser(intent, "Export CSV"))
		}) { Text("Export CSV") }
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

@Composable
private fun AxisBarChart(labels: List<String>, values: List<Double>, barColor: Color = Color(0xFF2196F3), maxY: Double) {
	val max = maxY.coerceAtLeast(1.0)
	Canvas(Modifier.fillMaxWidth().height(200.dp)) {
		val leftPadding = 48f
		val bottomPadding = 36f
		val chartWidth = size.width - leftPadding
		val chartHeight = size.height - bottomPadding
		// y-axis
		drawLine(Color.Gray, start = androidx.compose.ui.geometry.Offset(leftPadding, 0f), end = androidx.compose.ui.geometry.Offset(leftPadding, chartHeight))
		// x-axis
		drawLine(Color.Gray, start = androidx.compose.ui.geometry.Offset(leftPadding, chartHeight), end = androidx.compose.ui.geometry.Offset(size.width, chartHeight))
		val barWidth = chartWidth / (values.size * 2f)
		values.forEachIndexed { index, v ->
			val height = (v / max).toFloat() * (chartHeight - 4f)
			val x = leftPadding + (index * 2 + 1) * barWidth
			drawRect(
				color = barColor,
				topLeft = androidx.compose.ui.geometry.Offset(x = x, y = chartHeight - height),
				size = androidx.compose.ui.geometry.Size(width = barWidth, height = height)
			)
		}
	}
	Row(Modifier.fillMaxWidth().padding(horizontal = 48.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
		labels.forEach { lbl -> Text(lbl, style = TextStyle(fontSize = 10.sp)) }
	}
}

@Composable
private fun AxisLineChart(labels: List<String>, values: List<Double>, lineColor: Color = Color(0xFF4CAF50), maxY: Double) {
	val max = maxY.coerceAtLeast(1.0)
	Canvas(Modifier.fillMaxWidth().height(200.dp)) {
		val leftPadding = 48f
		val bottomPadding = 36f
		val chartWidth = size.width - leftPadding
		val chartHeight = size.height - bottomPadding
		// axes
		drawLine(Color.Gray, start = androidx.compose.ui.geometry.Offset(leftPadding, 0f), end = androidx.compose.ui.geometry.Offset(leftPadding, chartHeight))
		drawLine(Color.Gray, start = androidx.compose.ui.geometry.Offset(leftPadding, chartHeight), end = androidx.compose.ui.geometry.Offset(size.width, chartHeight))
		val step = if (values.size > 1) chartWidth / (values.size - 1) else 0f
		var prevX = 0f
		var prevY = 0f
		values.forEachIndexed { index, v ->
			val x = leftPadding + step * index
			val y = chartHeight - (v / max).toFloat() * (chartHeight - 4f)
			if (index > 0) {
				drawLine(lineColor, start = androidx.compose.ui.geometry.Offset(prevX, prevY), end = androidx.compose.ui.geometry.Offset(x, y), strokeWidth = 4f)
			}
			prevX = x
			prevY = y
		}
	}
	Row(Modifier.fillMaxWidth().padding(horizontal = 48.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
		labels.forEach { lbl -> Text(lbl, style = TextStyle(fontSize = 10.sp)) }
	}
}

