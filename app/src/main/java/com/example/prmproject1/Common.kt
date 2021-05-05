package com.example.prmproject1

object Common {
    const val TRANSACTIONS_DATABASE_NAME = "transactions.db"

    const val ADD_TRANSACTION_REQUEST_CODE = 1
    const val MODIFY_TRANSACTION_REQUEST_CODE = 2

    const val INTENT_DATA_POSITION = "INTENT_DATA_POSITION"
    const val INTENT_DATA_VALUE = "INTENT_DATA_VALUE"
    const val INTENT_DATA_DATE = "INTENT_DATA_DATE"
    const val INTENT_DATA_CATEGORY = "INTENT_DATA_CATEGORY"
    const val INTENT_DESCRIPTION_DATA = "INTENT_DESCRIPTION_DATA"

    val CATEGORIES = mapOf(
        "rachunki" to R.color.category_bills,
        "jedzenie" to R.color.category_food,
        "zdrowie" to R.color.category_health,
        "rozrywka" to R.color.category_entertainment,
        "przychód" to R.color.category_income
    )
}