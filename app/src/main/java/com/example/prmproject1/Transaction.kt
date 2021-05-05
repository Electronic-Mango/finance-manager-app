package com.example.prmproject1

import java.time.LocalDateTime

/**
 * Data class storing information about a single transaction.
 */
data class Transaction(
    var id: Int,
    var value: Int,
    var date: LocalDateTime,
    var category: String,
    var description: String
)
