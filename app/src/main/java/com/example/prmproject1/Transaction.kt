package com.example.prmproject1

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Data class storing information about a single transaction.
 */
@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var value: Double,
    var date: LocalDate,
    var category: String,
    var description: String
)
