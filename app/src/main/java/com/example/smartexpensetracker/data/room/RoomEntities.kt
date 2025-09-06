package com.example.smartexpensetracker.data.room

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.example.smartexpensetracker.data.model.Expense
import com.example.smartexpensetracker.data.model.ExpenseCategory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

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
	dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault()),
	receiptImageUri = receiptImageUri
)

fun Expense.toEntity(): ExpenseEntity = ExpenseEntity(
	id = id,
	title = title,
	amountInRupees = amountInRupees,
	category = category.name,
	notes = notes,
	epochMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
	receiptImageUri = receiptImageUri
)

@Dao
interface ExpenseDao {
	@Query("SELECT * FROM expenses ORDER BY epochMillis DESC")
	suspend fun getAllOnce(): List<ExpenseEntity>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun upsert(entity: ExpenseEntity)
}

@Database(entities = [ExpenseEntity::class], version = 1, exportSchema = false)
abstract class ExpenseDatabase : RoomDatabase() {
	abstract fun dao(): ExpenseDao
}