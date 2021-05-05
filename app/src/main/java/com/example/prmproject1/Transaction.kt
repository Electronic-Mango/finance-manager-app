package com.example.prmproject1

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Data class storing information about a single transaction.
 */
data class Transaction(
    var id: Int,
    var value: Float,
    var date: LocalDate,
    var category: String,
    var description: String
)
