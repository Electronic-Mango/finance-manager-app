package com.example.prmproject1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.prmproject1.Common.ADD_TRANSACTION_REQUEST_CODE
import com.example.prmproject1.Common.CATEGORY_DATA_RESULT
import com.example.prmproject1.Common.DATE_DATA_RESULT
import com.example.prmproject1.Common.DESCRIPTION_DATA_RESULT
import com.example.prmproject1.Common.VALUE_DATA_RESULT
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main_activity.*
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import java.util.stream.Collectors

/**
 * Main [AppCompatActivity] of the app, displays all recorded transactions.
 */
class MainActivity : AppCompatActivity() {

    private val transactions = getInitialTransactions(10)
    private val allTransactionsFragment = TransactionListFragment(transactions)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbarActivityMain)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)

        supportFragmentManager.beginTransaction()
            .add(R.id.mainActivityBottomContainer, allTransactionsFragment, "ALL_TRANSACTIONS")
            .commit()


        fabActivityMain.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivityForResult(intent, ADD_TRANSACTION_REQUEST_CODE)
        }
    }

    override fun onStart() {
        super.onStart()
        updateMonthlySummary()
    }

    private fun updateMonthlySummary () {
        val currentDate = LocalDate.now()
        summaryCurrentMonth.text = "${currentDate.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("pl")).toUpperCase()} ${currentDate.year}"
        val currentMonthTransactions = transactions.stream().filter { transaction ->
            transaction.date.monthValue == currentDate.monthValue && transaction.date.year == currentDate.year
        }.map(Transaction::value).collect(Collectors.toList())
        val income = currentMonthTransactions.stream().mapToDouble(Double::toDouble).filter { value -> value > 0.0 }.sum()
        val expenses = currentMonthTransactions.stream().mapToDouble(Double::toDouble).filter { value -> value < 0.0 }.sum()
        val balance = income + expenses
        summaryCurrentIncome.text = income.toString()
        summaryCurrentExpenses.text = expenses.toString()
        summaryCurrentBalance.text = balance.toString()
        if (balance > 0) {
            summaryCurrentBalance.setTextColor(ContextCompat.getColor(this, R.color.green))
        } else {
            summaryCurrentBalance.setTextColor(ContextCompat.getColor(this, R.color.red))
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
                transactions.add(newTransaction)
                transactionRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun extractTransactionFromIntentData(data: Intent): Transaction {
        val value = data.getDoubleExtra(VALUE_DATA_RESULT, 0.0)
        val date = data.getSerializableExtra(DATE_DATA_RESULT) as LocalDate
        val category = data.getStringExtra(CATEGORY_DATA_RESULT).toString()
        val description = data.getStringExtra(DESCRIPTION_DATA_RESULT).toString()
        Log.d("ADD_TRANSACTION", "$value $date $category $description")
        return Transaction(1, value, date, category, description)
    }

    fun switchToGraphView(view: View) {
        Snackbar.make(this, view, "Switching to graph...", Snackbar.LENGTH_LONG).show()
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
