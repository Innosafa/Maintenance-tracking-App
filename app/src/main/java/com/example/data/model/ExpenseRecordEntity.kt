package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_records")
data class ExpenseRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val assetName: String,
    val date: Long = System.currentTimeMillis(),
    val expenseCategory: String, // "Parts & Materials", "Labor & Overtime", "Vendor/Contractor", "Emergency Dispatch", "Lubricants & Consumables", "Inspection/Testing"
    val amount: Double,
    val invoiceRef: String,
    val description: String,
    val vendor: String
)
