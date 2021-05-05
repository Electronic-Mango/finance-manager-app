package com.example.prmproject1

import java.time.LocalDate

/**
 * Data class storing information about a single transaction.
 */
data class Transaction(
    var id: Int,
    var value: Double,
    var date: LocalDate,
    var category: String,
    var description: String
)
