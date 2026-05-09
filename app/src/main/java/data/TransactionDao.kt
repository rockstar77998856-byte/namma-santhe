package com.example.nammasantheledger.data

import androidx.room.*

@Dao
interface TransactionDao {
    @Insert
    fun insert(transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Query("DELETE FROM transactions")
    fun deleteAll()

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAll(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE phoneNumber != '' GROUP BY customerName")
    fun getCustomersWithPhone(): List<Transaction>

    // NEW: Useful for calculating a specific customer's net balance
    @Query("SELECT * FROM transactions WHERE customerName = :name")
    fun getTransactionsByCustomer(name: String): List<Transaction>

    @Update
    fun update(transaction: Transaction)
}