package com.example.prmproject1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import com.example.prmproject1.Common.ADD_TRANSACTION_REQUEST_CODE
import com.example.prmproject1.Common.INTENT_DATA_CATEGORY
import com.example.prmproject1.Common.INTENT_DATA_DATE
import com.example.prmproject1.Common.INTENT_DESCRIPTION_DATA
import com.example.prmproject1.Common.MODIFY_TRANSACTION_REQUEST_CODE
import com.example.prmproject1.Common.INTENT_DATA_POSITION
import com.example.prmproject1.Common.INTENT_DATA_VALUE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main_activity.*
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import java.time.LocalDate
import java.time.format.TextStyle.FULL_STANDALONE
import java.util.*
import java.util.stream.Collectors

/**
 * Main [AppCompatActivity] of the app, displays all recorded transactions.
 */
class MainActivity : AppCompatActivity() {

    private val transactions = getInitialTransactions(10)
    private val allTransactionsFragment = TransactionListFragment(transactions)
    private val monthSummaryFragment = MonthSummaryFragment()
    private var showingTransactionList = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbarActivityMain)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)

        switchToGraphView(contentMainActivityLayout.rootView)

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
        val currentMonth = currentDate.month
            .getDisplayName(FULL_STANDALONE, Locale("pl"))
            .toUpperCase(Locale.ROOT)

        summaryCurrentMonth.text = getString(R.string.summary_current_month_text, currentMonth, currentDate.year)

        val currentMonthTransactions = transactions.stream().filter { transaction ->
            transaction.date.monthValue == currentDate.monthValue && transaction.date.year == currentDate.year
        }.map(Transaction::value).collect(Collectors.toList())
        val income = currentMonthTransactions.stream().mapToDouble(Double::toDouble).filter { value -> value > 0.0 }.sum()
        val expenses = currentMonthTransactions.stream().mapToDouble(Double::toDouble).filter { value -> value < 0.0 }.sum()
        val balance = income + expenses

        summaryCurrentIncome.text = income.toString()
        summaryCurrentExpenses.text = expenses.toString()
        summaryCurrentBalance.text = balance.toString()
        summaryCurrentBalance.setTextColor(getColor(this, if (balance > 0) R.color.balance_positive else R.color.balance_negative))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ADD_TRANSACTION_REQUEST_CODE -> data?.let { handleAddTransactionResult(resultCode, it) }
            MODIFY_TRANSACTION_REQUEST_CODE -> data?.let { handleModifyTransactionResult(resultCode, it) }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleAddTransactionResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            RESULT_CANCELED -> Log.d("MainActivityLog", "Cancelled creating new transaction")
            RESULT_OK -> {
                val newTransaction = extractTransactionFromIntentData(data)
                transactions.add(newTransaction)
                transactionRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun handleModifyTransactionResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            RESULT_CANCELED -> Log.d("MainActivityLog", "Cancelled modifying transaction")
            RESULT_OK -> {
                val updatedTransaction = extractTransactionFromIntentData(data)
                val position = data.getIntExtra(INTENT_DATA_POSITION, -1)
                transactions[position] = updatedTransaction
                transactionRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    fun handleRemoveTransaction(position: Int) {
        transactions.removeAt(position)
        transactionRecyclerView.adapter?.notifyDataSetChanged()
        updateMonthlySummary()
    }

    private fun extractTransactionFromIntentData(data: Intent): Transaction {
        val value = data.getDoubleExtra(INTENT_DATA_VALUE, 0.0)
        val date = data.getSerializableExtra(INTENT_DATA_DATE) as LocalDate
        val category = data.getStringExtra(INTENT_DATA_CATEGORY).toString()
        val description = data.getStringExtra(INTENT_DESCRIPTION_DATA).toString()
        return Transaction(0, value, date, category, description)
    }

    fun switchToGraphView(view: View) {
        val fragmentToShow = if (showingTransactionList) allTransactionsFragment else monthSummaryFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainActivityBottomContainer, fragmentToShow, "MONTH_SUMMARY_GRAPH")
            .commit()
        val iconId = if (showingTransactionList) R.drawable.ic_balance_chart else R.drawable.ic_transaction_list
        imageButton?.setImageDrawable(getDrawable(applicationContext, iconId))
        showingTransactionList = !showingTransactionList
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
