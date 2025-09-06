package com.example.smartexpensetracker.ui.report.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BarChartView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

	private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = Color.GRAY
		strokeWidth = 2f
	}
	private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = Color.LTGRAY
		strokeWidth = 1f
	}
	private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = Color.parseColor("#2196F3")
	}
	private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = Color.DKGRAY
		textAlign = Paint.Align.CENTER
		textSize = 28f
	}
	private val yLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = Color.DKGRAY
		textAlign = Paint.Align.RIGHT
		textSize = 26f
	}

	private var dates: List<LocalDate> = emptyList()
	private var amounts: List<Double> = emptyList()
	private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")

	fun setData(dates: List<LocalDate>, amounts: List<Double>) {
		this.dates = dates
		this.amounts = amounts
		invalidate()
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		if (dates.isEmpty() || amounts.isEmpty()) return

		val leftPadding = 70f
		val rightPadding = 20f
		val topPadding = 20f
		val bottomPadding = 44f

		val width = width - leftPadding - rightPadding
		val height = height - topPadding - bottomPadding

		// Axes
		canvas.drawLine(leftPadding, topPadding, leftPadding, topPadding + height, axisPaint)
		canvas.drawLine(leftPadding, topPadding + height, leftPadding + width, topPadding + height, axisPaint)

		val max = amounts.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0

		// Grid lines and Y-axis amount labels (0%, 50%, 100%)
		for (i in 0..2) {
			val fraction = i / 2f
			val y = topPadding + height - fraction * height
			canvas.drawLine(leftPadding, y, leftPadding + width, y, gridPaint)
			val value = max * fraction
			canvas.drawText("₹" + String.format("%.0f", value), leftPadding - 8f, y + 8f, yLabelPaint)
		}

		// Bars
		val barSpace = width / (amounts.size * 2f)
		amounts.forEachIndexed { index, value ->
			val left = leftPadding + (index * 2 + 0.5f) * barSpace
			val right = left + barSpace
			val barHeight = ((value / max).toFloat()) * height
			val top = topPadding + height - barHeight
			canvas.drawRect(left, top, right, topPadding + height, barPaint)

			// X label (date)
			canvas.drawText(dates[index].format(dateFormatter), (left + right) / 2f, topPadding + height + 30f, labelPaint)
		}
	}
}