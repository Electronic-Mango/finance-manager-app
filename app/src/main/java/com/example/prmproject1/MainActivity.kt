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
import androidx.room.Room
import com.example.prmproject1.Common.ADD_TRANSACTION_REQUEST_CODE
import com.example.prmproject1.Common.INTENT_DATA_CATEGORY
import com.example.prmproject1.Common.INTENT_DATA_DATE
import com.example.prmproject1.Common.INTENT_DESCRIPTION_DATA
import com.example.prmproject1.Common.MODIFY_TRANSACTION_REQUEST_CODE
import com.example.prmproject1.Common.INTENT_DATA_POSITION
import com.example.prmproject1.Common.INTENT_DATA_VALUE
import com.example.prmproject1.database.Transaction
import com.example.prmproject1.database.TransactionDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main_activity.*
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import java.time.LocalDate
import java.time.format.TextStyle.FULL_STANDALONE
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

/**
 * Main [AppCompatActivity] of the app, displays all recorded transactions.
 */
class MainActivity : AppCompatActivity() {

    private val transactions = ArrayList<Transaction>()
    private val allTransactionsFragment = TransactionListFragment(transactions)
    private val monthBalanceFragment = MonthBalanceGraph()
    private var showingTransactionList = true
    private val database by lazy {
        Room.databaseBuilder(this, TransactionDatabase::class.java, "transactions.sb").build()
    }

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

        thread {
            val initialTransactions = database.transactionDao().getAll()
            runOnUiThread {
                transactions.addAll(initialTransactions)
                transactionRecyclerView.adapter?.notifyDataSetChanged()
                updateMonthlySummary()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateMonthlySummary()
    }

    private fun updateMonthlySummary() {
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
        val balanceColor = when {
            balance > 0.0 -> {
                R.color.balance_positive
            }
            balance < 0.0 -> {
                R.color.balance_negative
            }
            else -> {
                R.color.black
            }
        }
        summaryCurrentBalance.setTextColor(getColor(this, balanceColor))
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
                thread {
                    database.transactionDao().insert(newTransaction)
                    runOnUiThread {
                        transactions.add(newTransaction)
                        transactionRecyclerView.adapter?.notifyDataSetChanged()
                        updateMonthlySummary()
                    }
                }
            }
        }
    }

    private fun handleModifyTransactionResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            RESULT_CANCELED -> Log.d("MainActivityLog", "Cancelled modifying transaction")
            RESULT_OK -> {
                val position = data.getIntExtra(INTENT_DATA_POSITION, -1)
                val id = transactions[position].id
                val updatedTransaction = extractTransactionFromIntentData(data, id)
                thread {
                    database.transactionDao().update(updatedTransaction)
                    runOnUiThread {
                        transactions[position] = updatedTransaction
                        transactionRecyclerView.adapter?.notifyDataSetChanged()
                        updateMonthlySummary()
                    }
                }
            }
        }
    }

    fun handleRemoveTransaction(position: Int) {
        val transactionToRemove = transactions[position]
        thread {
            database.transactionDao().delete(transactionToRemove)
            runOnUiThread {
                transactions.remove(transactionToRemove)
                transactionRecyclerView.adapter?.notifyDataSetChanged()
                updateMonthlySummary()
            }
        }
    }

    private fun extractTransactionFromIntentData(data: Intent, id: Int = 0): Transaction {
        val value = data.getDoubleExtra(INTENT_DATA_VALUE, 0.0)
        val date = data.getSerializableExtra(INTENT_DATA_DATE).toString().let { LocalDate.parse(it) }
        val category = data.getStringExtra(INTENT_DATA_CATEGORY).toString()
        val description = data.getStringExtra(INTENT_DESCRIPTION_DATA).toString()
        return Transaction(id, value, date, category, description)
    }

    fun switchToGraphView(view: View) {
        val fragmentToShow = if (showingTransactionList) allTransactionsFragment else monthBalanceFragment
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
