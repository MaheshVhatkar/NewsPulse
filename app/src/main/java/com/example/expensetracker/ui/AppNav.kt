package com.example.expensetracker.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.screens.ExpenseEntryScreen
import com.example.expensetracker.ui.screens.ExpenseListScreen
import com.example.expensetracker.ui.screens.ExpenseReportScreen

sealed class Dest(val route: String, val label: String, val icon: ImageVector)

@Composable
fun AppNav(dark: Boolean, onToggleDark: () -> Unit) {
	val navController = rememberNavController()

	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(title = { Text("Expense Tracker") }, actions = {
				IconButton(onClick = onToggleDark) {
					Icon(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode, contentDescription = "Toggle theme")
				}
			})
		},
		bottomBar = {
			NavigationBar {
				val items = BottomNavItems.all
				val navBackStackEntry by navController.currentBackStackEntryAsState()
				val currentDestination = navBackStackEntry?.destination
				items.forEach { item ->
					NavigationBarItem(
						selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
						onClick = {
							navController.navigate(item.route) {
								launchSingleTop = true
								restoreState = true
								popUpTo(navController.graph.startDestinationId) {
									saveState = true
								}
							}
						},
						icon = { Icon(item.icon, contentDescription = item.label) },
						label = { Text(item.label) }
					)
				}
			}
		}
	) { padding ->
		NavHost(
			navController = navController,
			startDestination = BottomNavItems.Entry.route
		) {
			composable(BottomNavItems.Entry.route) { ExpenseEntryScreen(padding) { navController.navigate(BottomNavItems.List.route) } }
			composable(BottomNavItems.List.route) { ExpenseListScreen(padding) }
			composable(BottomNavItems.Report.route) { ExpenseReportScreen(padding) }
		}
	}
}

