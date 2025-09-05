package com.example.smartexpensetracker.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexpensetracker.data.model.Expense
import com.example.smartexpensetracker.databinding.ItemExpenseBinding
import com.example.smartexpensetracker.databinding.ItemHeaderBinding

class ExpenseAdapter : ListAdapter<ExpenseListItem, RecyclerView.ViewHolder>(Diff) {
	private companion object {
		const val TYPE_HEADER = 0
		const val TYPE_ROW = 1
	}

	object Diff : DiffUtil.ItemCallback<ExpenseListItem>() {
		override fun areItemsTheSame(oldItem: ExpenseListItem, newItem: ExpenseListItem): Boolean =
			when {
				oldItem is ExpenseListItem.Header && newItem is ExpenseListItem.Header -> oldItem.title == newItem.title
				oldItem is ExpenseListItem.Row && newItem is ExpenseListItem.Row -> oldItem.expense.id == newItem.expense.id
				else -> false
			}

		override fun areContentsTheSame(oldItem: ExpenseListItem, newItem: ExpenseListItem): Boolean = oldItem == newItem
	}

	class HeaderVH(val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root)
	class RowVH(val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root)

	override fun getItemViewType(position: Int): Int = when (getItem(position)) {
		is ExpenseListItem.Header -> TYPE_HEADER
		is ExpenseListItem.Row -> TYPE_ROW
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		return when (viewType) {
			TYPE_HEADER -> HeaderVH(ItemHeaderBinding.inflate(inflater, parent, false))
			else -> RowVH(ItemExpenseBinding.inflate(inflater, parent, false))
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		when (val item = getItem(position)) {
			is ExpenseListItem.Header -> (holder as HeaderVH).binding.textHeader.text = item.title
			is ExpenseListItem.Row -> bindRow(holder as RowVH, item.expense)
		}
	}

	private fun bindRow(holder: RowVH, item: Expense) {
		holder.binding.textTitle.text = item.title
		holder.binding.textAmount.text = "₹${item.amountInRupees}"
		holder.binding.textMeta.text = "${item.category} • ${item.dateTime.toLocalTime()}"
	}
}