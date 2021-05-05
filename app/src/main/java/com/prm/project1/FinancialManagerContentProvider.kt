package com.prm.project1

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import com.prm.project1.database.TransactionDatabase

class FinancialManagerContentProvider : ContentProvider() {
    private lateinit var database: TransactionDatabase

    override fun onCreate(): Boolean {
        database = Room.databaseBuilder(context!!, TransactionDatabase::class.java, Common.TRANSACTIONS_DATABASE_NAME)
            .build()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        return when (URI_MATCHER.match(uri)) {
            GET_TABLE -> {
                database.transactionDao().getAllRaw()
            }
            GET_ROW -> {
                val id = uri.lastPathSegment!!.toInt()
                database.transactionDao().getSingleTransactionRaw(id)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String {
        return when (URI_MATCHER.match(uri)) {
            GET_TABLE -> {
                TRANSACTIONS_MIME_TYPE
            }
            GET_ROW -> {
                TRANSACTION_MIME_TYPE
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("Cannot insert data!")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("Cannot delete data!")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("Cannot update data!")
    }
}

private const val PATH = "transactions"
private const val AUTHORITY = "com.prm.project1.provider"
private const val GET_TABLE = 1
private const val GET_ROW = 2

private val URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
    addURI(AUTHORITY, PATH, GET_TABLE)
    addURI(AUTHORITY, "$PATH/#", GET_ROW)
}

private const val TRANSACTIONS_MIME_TYPE = "vnd.android.cursor.dir/vnd.$AUTHORITY.$PATH"
private const val TRANSACTION_MIME_TYPE = "vnd.android.cursor.item/vnd.$AUTHORITY.$PATH"