package com.example.smartexpensetracker.di

import com.example.smartexpensetracker.data.ExpenseRepository

object ServiceLocator {
	val repository: ExpenseRepository by lazy { ExpenseRepository() }
}