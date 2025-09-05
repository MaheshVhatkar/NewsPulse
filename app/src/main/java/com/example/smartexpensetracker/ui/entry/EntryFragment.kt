package com.example.smartexpensetracker.ui.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.smartexpensetracker.R
import com.example.smartexpensetracker.data.model.ExpenseCategory
import com.example.smartexpensetracker.databinding.FragmentEntryBinding
import com.example.smartexpensetracker.ui.ViewModelFactory
import kotlinx.coroutines.launch

class EntryFragment : Fragment() {
	private var _binding: FragmentEntryBinding? = null
	private val binding get() = _binding!!

	private val viewModel: EntryViewModel by viewModels { ViewModelFactory() }

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentEntryBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val categories = ExpenseCategory.values().map { it.name }
		binding.spinnerCategory.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)

		binding.buttonSubmit.setOnClickListener {
			val result = viewModel.addExpense(
				title = binding.inputTitle.text?.toString().orEmpty(),
				amountRupees = binding.inputAmount.text?.toString().orEmpty(),
				category = ExpenseCategory.valueOf(binding.spinnerCategory.selectedItem as String),
				notes = binding.inputNotes.text?.toString(),
				receiptImageUri = null
			)
			result.onSuccess {
				Toast.makeText(requireContext(), getString(R.string.added_success), Toast.LENGTH_SHORT).show()
				binding.containerEntry.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in))
				clearInputs()
			}.onFailure {
				Toast.makeText(requireContext(), it.message ?: "Error", Toast.LENGTH_SHORT).show()
			}
		}

		viewModel.refreshTodayTotal()
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				viewModel.todayTotal.collect { total ->
					binding.textTodayTotal.text = getString(R.string.today_total_format, total)
				}
			}
		}
	}

	private fun clearInputs() {
		binding.inputTitle.setText("")
		binding.inputAmount.setText("")
		binding.inputNotes.setText("")
		binding.spinnerCategory.setSelection(0)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}