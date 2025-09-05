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
import com.example.smartexpensetracker.BuildConfig
import com.example.smartexpensetracker.databinding.FragmentReportBinding
import com.example.smartexpensetracker.ui.ViewModelFactory
import java.io.File

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
		val catTotals = viewModel.categoryTotalsLast7Days()

		binding.textDaily.text = daily.joinToString(separator = "\n") { "${it.date}: ₹${it.total}" }
		binding.textCategory.text = catTotals.entries.joinToString(separator = "\n") { "${it.key}: ₹${it.value}" }

		binding.buttonExport.setOnClickListener { exportCsv(daily) }
	}

	private fun exportCsv(rows: List<ReportViewModel.DailyTotal>) {
		val file = File(requireContext().cacheDir, "expense_report.csv")
		val content = buildString {
			appendLine("Date,Total")
			rows.forEach { appendLine("${it.date},${it.total}") }
		}
		file.writeText(content)
		val uri: Uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", file)
		val intent = Intent(Intent.ACTION_SEND).apply {
			type = "text/csv"
			putExtra(Intent.EXTRA_STREAM, uri)
			addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
		}
		startActivity(Intent.createChooser(intent, "Share CSV"))
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}