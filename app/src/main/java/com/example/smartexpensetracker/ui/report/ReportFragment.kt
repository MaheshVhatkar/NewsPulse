package com.example.smartexpensetracker.ui.report

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.smartexpensetracker.databinding.FragmentReportBinding
import com.example.smartexpensetracker.ui.ViewModelFactory
import com.example.smartexpensetracker.di.ServiceLocator
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReportFragment : Fragment() {
	private var _binding: FragmentReportBinding? = null
	private val binding get() = _binding!!
	private val viewModel: ReportViewModel by viewModels { ViewModelFactory() }

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentReportBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val daily = viewModel.last7DaysTotals()
		binding.chart.visibility = View.VISIBLE
		binding.chart.setData(daily.map { it.date }, daily.map { it.total })

		val categoryTotals = viewModel.categoryTotalsLast7Days()
		binding.heading_category.visibility = View.VISIBLE
		binding.chart_category.visibility = View.VISIBLE
		binding.chart_category.setDataLabels(
			labels = categoryTotals.keys.map { it.name },
			amounts = categoryTotals.values.toList()
		)

		binding.button_export.setOnClickListener { exportCsvLast7() }
	}

	private fun exportCsvLast7() {
		val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
		val today = LocalDate.now()
		val last7 = (0..6).map { today.minusDays(it.toLong()) }.toSet()
		val expenses = ServiceLocator.repository.getAll()
			.filter { it.date() in last7 }
			.sortedBy { it.dateTime }

		val csv = buildString {
			appendLine("date time,category,expense amount")
			expenses.forEach { e ->
				appendLine("${e.dateTime.format(formatter)},${e.category},${"%.2f".format(e.amountInRupees)}")
			}
		}

		val file = File(requireContext().cacheDir, "expenses_last7.csv")
		file.writeText(csv)
		val uri: Uri = FileProvider.getUriForFile(requireContext(), requireContext().packageName + ".provider", file)
		val intent = Intent(Intent.ACTION_SEND).apply {
			type = "text/csv"
			putExtra(Intent.EXTRA_STREAM, uri)
			addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
		}
		startActivity(Intent.createChooser(intent, "Export CSV"))
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}