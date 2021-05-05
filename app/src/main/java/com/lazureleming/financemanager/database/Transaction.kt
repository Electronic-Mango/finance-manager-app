package com.lazureleming.financemanager.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Data class storing information about a single transaction.
 */
@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val value: Double,
    val date: LocalDate,
    val category: String,
    val place: String
)
