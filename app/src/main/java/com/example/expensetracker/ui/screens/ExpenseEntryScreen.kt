package com.example.expensetracker.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.SmartExpenseApp
import com.example.expensetracker.data.ExpenseCategory
import com.example.expensetracker.ui.vm.ExpenseViewModel
import com.example.expensetracker.ui.vm.ExpenseViewModelFactory
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
	padding: PaddingValues,
	vm: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory((LocalContext.current.applicationContext as SmartExpenseApp).repository)),
	onAdded: () -> Unit = {}
) {
	val context = LocalContext.current
	val form = vm.form.collectAsState().value
	val todayTotal = vm.totalSpentOn(LocalDate.now())

	val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
		if (uri != null) {
			try {
				context.contentResolver.takePersistableUriPermission(
					uri,
					Intent.FLAG_GRANT_READ_URI_PERMISSION
				)
			} catch (_: SecurityException) {}
			vm.onReceiptChange(uri.toString())
		}
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
			.padding(16.dp),
		verticalArrangement = Arrangement.Top
	) {
		Text("Total Spent Today: ₹%.2f".format(todayTotal))
		Spacer(Modifier.height(12.dp))
		OutlinedTextField(
			value = form.title,
			onValueChange = vm::onTitleChange,
			label = { Text("Title") },
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(Modifier.height(8.dp))
		OutlinedTextField(
			value = form.amount,
			onValueChange = vm::onAmountChange,
			label = { Text("Amount (₹)") },
			keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Number),
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(Modifier.height(8.dp))
		var expanded by remember { mutableStateOf(false) }
		Row(verticalAlignment = Alignment.CenterVertically) {
			Text("Category: ${form.category}")
			Spacer(Modifier.weight(1f))
			TextButton(onClick = { expanded = true }) { Text("Change") }
			DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
				ExpenseCategory.entries.forEach { cat ->
					DropdownMenuItem(text = { Text(cat.name) }, onClick = {
						vm.onCategoryChange(cat)
						expanded = false
					})
				}
			}
		}
		Spacer(Modifier.height(8.dp))
		OutlinedTextField(
			value = form.notes,
			onValueChange = vm::onNotesChange,
			label = { Text("Notes (optional, max 100)") },
			modifier = Modifier.fillMaxWidth(),
			maxLines = 3
		)
		Spacer(Modifier.height(12.dp))
		Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
			TextButton(onClick = { imagePicker.launch(arrayOf("image/*")) }) {
				Text(if (form.receiptUri == null) "Attach Receipt" else "Receipt Attached")
			}
		}
		Spacer(Modifier.height(16.dp))
		Button(onClick = {
			vm.addExpense {
				Toast.makeText(context, "Expense added", Toast.LENGTH_SHORT).show()
				onAdded()
			}
		}, modifier = Modifier.fillMaxWidth()) {
			Text("Submit")
		}

		AnimatedVisibility(visible = form.error != null, enter = fadeIn(), exit = fadeOut()) {
			Text(form.error ?: "")
		}
	}

	// Removed spurious toast on open
}

