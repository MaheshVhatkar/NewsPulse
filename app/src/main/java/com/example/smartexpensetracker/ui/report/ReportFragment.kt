package com.example.smartexpensetracker.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.smartexpensetracker.databinding.FragmentReportBinding
import com.example.smartexpensetracker.ui.ViewModelFactory

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
		binding.textDaily.visibility = View.GONE
		binding.textCategory.visibility = View.GONE
		binding.chart.visibility = View.VISIBLE
		binding.chart.setData(daily.map { it.date }, daily.map { it.total })
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}