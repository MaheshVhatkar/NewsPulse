package com.example.expensetracker.data

import androidx.room.ColumnInfo
import androidx.room.Database
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "expenses")
data class ExpenseEntity(
	@PrimaryKey val id: String,
	@ColumnInfo(name = "title") val title: String,
	@ColumnInfo(name = "amount") val amountInRupees: Double,
	@ColumnInfo(name = "category") val category: String,
	@ColumnInfo(name = "notes") val notes: String?,
	@ColumnInfo(name = "epochMillis") val epochMillis: Long,
	@ColumnInfo(name = "receiptUri") val receiptImageUri: String?
)

fun ExpenseEntity.toDomain(): Expense = Expense(
	id = id,
	title = title,
	amountInRupees = amountInRupees,
	category = ExpenseCategory.valueOf(category),
	notes = notes,
	epochMillis = epochMillis,
	receiptImageUri = receiptImageUri
)

fun Expense.toEntity(): ExpenseEntity = ExpenseEntity(
	id = id,
	title = title,
	amountInRupees = amountInRupees,
	category = category.name,
	notes = notes,
	epochMillis = epochMillis,
	receiptImageUri = receiptImageUri
)

@Dao
interface ExpenseDao {
	@Query("SELECT * FROM expenses ORDER BY epochMillis DESC")
	fun observeAll(): Flow<List<ExpenseEntity>>

	@Query("SELECT COUNT(*) FROM expenses")
	suspend fun count(): Long

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun upsert(entity: ExpenseEntity)

	@Query("DELETE FROM expenses WHERE id = :id")
	suspend fun deleteById(id: String)
}

@Database(entities = [ExpenseEntity::class], version = 1, exportSchema = false)
abstract class ExpenseDatabase : RoomDatabase() {
	abstract fun dao(): ExpenseDao
}

