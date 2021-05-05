package com.example.prmproject1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.prmproject1.Common.ADD_TRANSACTION_REQUEST_CODE
import com.example.prmproject1.Common.CATEGORY_DATA_RESULT
import com.example.prmproject1.Common.DATE_DATA_RESULT
import com.example.prmproject1.Common.DESCRIPTION_DATA_RESULT
import com.example.prmproject1.Common.VALUE_DATA_RESULT
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Main [AppCompatActivity] of the app, displays all recorded transactions.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbarActivityMain)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)

        fabActivityMain.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivityForResult(intent, ADD_TRANSACTION_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ADD_TRANSACTION_REQUEST_CODE -> data?.let { handleAddTransactionResult(resultCode, it) }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleAddTransactionResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            RESULT_CANCELED -> Log.d("ADD_TRANSACTION", "Cancelled creating new transaction")
            RESULT_OK -> {
                val newTransaction = extractTransactionFromIntentData(data)
                (transactionList.adapter as TransactionRecyclerViewAdapter).addNewTransaction(newTransaction)
            }
        }
    }

    private fun extractTransactionFromIntentData(data: Intent): Transaction {
        val value: Float = data.getFloatExtra(VALUE_DATA_RESULT, 0f)
        val date = data.getStringExtra(DATE_DATA_RESULT).toString()
        val category = data.getStringExtra(CATEGORY_DATA_RESULT).toString()
        val description = data.getStringExtra(DESCRIPTION_DATA_RESULT).toString()
        Log.d("ADD_TRANSACTION", "Received new transaction: $value $date $category $description")
        return Transaction(1, value, LocalDate.now(), category, description)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}
