package com.example.nammasantheledger.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val customerName: String,
    val phoneNumber: String = "",   // ← NEW: for weekly SMS reminders
    var amount: Double,
    val timestamp: String,
    val transactionType: Int = 1
)