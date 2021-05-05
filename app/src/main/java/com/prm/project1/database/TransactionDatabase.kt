package com.prm.project1.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * [RoomDatabase] storing information about [Transaction].
 */
@Database(entities = [Transaction::class], version = 1)
@TypeConverters(LocalDateStringConverter::class)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}