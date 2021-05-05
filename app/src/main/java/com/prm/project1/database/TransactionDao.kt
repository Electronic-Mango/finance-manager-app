package com.prm.project1.database

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `Transaction`")
    fun getAll(): List<Transaction>

    @Insert
    fun insert(transaction: Transaction)

    @Update
    fun update(transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Query("SELECT * FROM `Transaction`")
    fun getAllRaw(): Cursor

    @Query("SELECT * FROM `Transaction` WHERE id = :id")
    fun getSingleTransactionRaw(id: Int): Cursor
}