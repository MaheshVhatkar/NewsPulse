package com.example.smartexpensetracker.ui.list

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartexpensetracker.data.model.Expense
import com.example.smartexpensetracker.databinding.FragmentListBinding
import com.example.smartexpensetracker.ui.ViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ListFragment : Fragment() {
	private var _binding: FragmentListBinding? = null
	private val binding get() = _binding!!

	private val viewModel: ListViewModel by viewModels { ViewModelFactory() }
	private lateinit var adapter: ExpenseAdapter

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentListBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		adapter = ExpenseAdapter()
		binding.recycler.layoutManager = LinearLayoutManager(requireContext())
		binding.recycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
		binding.recycler.adapter = adapter

		binding.buttonDate.setOnClickListener { showDatePicker() }
		binding.switchGroup.setOnCheckedChangeListener { _, _ -> refresh() }

		refresh()
	}

	private fun showDatePicker() {
		val date = viewModel.date.value
		DatePickerDialog(requireContext(), { _, y, m, d ->
			viewModel.setDate(LocalDate.of(y, m + 1, d))
			refresh()
		}, date.year, date.monthValue - 1, date.dayOfMonth).show()
	}

	private fun refresh() {
		val list = viewModel.expensesForSelectedDate()
		binding.textTotals.text = "Count: ${list.size}  Total: ₹${list.sumOf { it.amountInRupees }}"
		binding.empty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
		val items = composeItems(list, groupByCategory = binding.switchGroup.isChecked)
		adapter.submitList(items)
	}

	private fun composeItems(list: List<Expense>, groupByCategory: Boolean): List<ExpenseListItem> {
		if (list.isEmpty()) return emptyList()
		return if (groupByCategory) {
			list.groupBy { it.category.name }
				.toSortedMap()
				.flatMap { (header, items) ->
					listOf(ExpenseListItem.Header(header)) + items.map { ExpenseListItem.Row(it) }
				}
		} else {
			val fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
			list.sortedBy { it.dateTime }
				.groupBy { it.dateTime.format(fmt) }
				.flatMap { (header, items) ->
					listOf(ExpenseListItem.Header(header)) + items.map { ExpenseListItem.Row(it) }
				}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}