package com.example.expensetracker.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

object BottomNavItems {
	val Entry = Item("entry", "Add", Icons.Filled.Edit)
	val List = Item("list", "List", Icons.Filled.List)
	val Report = Item("report", "Report", Icons.Filled.Assessment)
	val all = listOf(Entry, List, Report)

	data class Item(val route: String, val label: String, val icon: ImageVector)
}

