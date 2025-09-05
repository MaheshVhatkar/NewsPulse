package com.example.smartexpensetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartexpensetracker.di.ServiceLocator
import com.example.smartexpensetracker.ui.entry.EntryViewModel
import com.example.smartexpensetracker.ui.list.ListViewModel
import com.example.smartexpensetracker.ui.report.ReportViewModel

class ViewModelFactory : ViewModelProvider.Factory {
	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		return when (modelClass) {
			EntryViewModel::class.java -> EntryViewModel(ServiceLocator.repository) as T
			ListViewModel::class.java -> ListViewModel(ServiceLocator.repository) as T
			ReportViewModel::class.java -> ReportViewModel(ServiceLocator.repository) as T
			else -> throw IllegalArgumentException("Unknown ViewModel ${modelClass.name}")
		}
	}
}