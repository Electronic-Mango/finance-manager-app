package com.lazureleming.financemanager

/**
 * Object containing constants used by entire application.
 */
object Common {

    const val DB_NAME = "transactions.db"

    const val ADD_TRANSACTION_REQUEST_CODE = 1
    const val MODIFY_TRANSACTION_REQUEST_CODE = 2

    const val INTENT_DATA_POSITION = "INTENT_DATA_POSITION"
    const val INTENT_DATA_VALUE = "INTENT_DATA_VALUE"
    const val INTENT_DATA_DATE = "INTENT_DATA_DATE"
    const val INTENT_DATA_CATEGORY = "INTENT_DATA_CATEGORY"
    const val INTENT_PLACE_DATA = "INTENT_PLACE_DATA"

    const val LARGEST_VALUE = 1000000000000000.0

    const val INCOME_CATEGORY = "Przychód"
    val CATEGORIES = mapOf(
        "Rachunki" to R.color.category_bills,
        "Jedzenie" to R.color.category_food,
        "Zakupy" to R.color.category_groceries,
        "Zdrowie" to R.color.category_health,
        "Rozrywka" to R.color.category_entertainment,
        "Transport" to R.color.category_transport,
        INCOME_CATEGORY to R.color.category_income
    )
}