package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.expensetracker.ui.AppNav
import com.example.expensetracker.ui.vm.ExpenseViewModel
import com.example.expensetracker.ui.vm.ExpenseViewModelFactory
import com.example.expensetracker.ui.theme.ExpenseTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			var dark by rememberSaveable { mutableStateOf(false) }
			ExpenseTheme(darkTheme = dark) {
				Surface(color = MaterialTheme.colorScheme.background) {
					AppNav(dark = dark, onToggleDark = { dark = !dark })
				}
			}
		}
	}
}

