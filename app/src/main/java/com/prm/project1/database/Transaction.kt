package com.prm.project1.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Data class storing information about a single transaction.
 */
@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val value: Double,
    val date: LocalDate,
    val category: String,
    val description: String
)
