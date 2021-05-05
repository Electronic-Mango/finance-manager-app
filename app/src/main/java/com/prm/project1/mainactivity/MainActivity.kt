package com.prm.project1.mainactivity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.room.Room
import com.prm.project1.Common.ADD_TRANSACTION_REQUEST_CODE
import com.prm.project1.Common.INTENT_DATA_CATEGORY
import com.prm.project1.Common.INTENT_DATA_DATE
import com.prm.project1.Common.INTENT_DATA_POSITION
import com.prm.project1.Common.INTENT_DATA_VALUE
import com.prm.project1.Common.INTENT_DESCRIPTION_DATA
import com.prm.project1.Common.MODIFY_TRANSACTION_REQUEST_CODE
import com.prm.project1.Common.TRANSACTIONS_DATABASE_NAME
import com.prm.project1.R
import com.prm.project1.addtransactionactivity.AddTransactionActivity
import com.prm.project1.database.Transaction
import com.prm.project1.database.TransactionDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main_activity.*
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.TextStyle.FULL_STANDALONE
import java.util.*
import java.util.function.DoublePredicate
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

/**
 * Main [AppCompatActivity] of the app, displays all recorded transactions.
 */
class MainActivity : AppCompatActivity() {
    private val transactions = ArrayList<Transaction>()
    private val allTransactionsFragment = TransactionListFragment(transactions)
    private val monthBalanceFragment = MonthBalanceGraph()
    private val database by lazy {
        Room.databaseBuilder(this, TransactionDatabase::class.java, TRANSACTIONS_DATABASE_NAME).build()
    }
    private var showingTransactionList = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbarActivityMain)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)

        thread {
            val initialTransactions = database.transactionDao().getAll()
            runOnUiThread {
                transactions.addAll(initialTransactions)
                transactionRecyclerView.adapter?.notifyDataSetChanged()
                updateMonthlySummary()
            }
        }

        switchBottomView()

        fabActivityMain.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivityForResult(intent, ADD_TRANSACTION_REQUEST_CODE)
        }
    }

    override fun onStart() {
        super.onStart()
        updateMonthlySummary()
        fabMainActivityBottomPanelSwitcher.setOnClickListener {
            switchBottomView()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ADD_TRANSACTION_REQUEST_CODE -> data?.let { handleAddTransactionResult(resultCode, it) }
            MODIFY_TRANSACTION_REQUEST_CODE -> data?.let { handleModifyTransactionResult(resultCode, it) }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun updateMonthlySummary() {
        val currentDate = LocalDate.now()
        val currentMonth = currentDate.month
            .getDisplayName(FULL_STANDALONE, Locale("pl"))
            .toUpperCase(Locale.ROOT)

        summaryCurrentMonth.text = getString(R.string.summary_current_month_text, currentMonth, currentDate.year)

        val income = getCurrentMonthTransactionsSum(currentDate) { value -> value > 0.0 }
        val expenses = getCurrentMonthTransactionsSum(currentDate) { value -> value < 0.0 }
        val balance = income + expenses

        summaryCurrentIncome.text = income.toPlainString()
        summaryCurrentExpenses.text = expenses.abs().toPlainString()
        summaryCurrentBalance.text = balance.abs().toPlainString()
        val balanceColor = when {
            balance.toDouble() > 0.0 -> R.color.balance_positive
            balance.toDouble() < 0.0 -> R.color.balance_negative
            else -> R.color.black
        }
        summaryCurrentBalance.setTextColor(getColor(this, balanceColor))
        val balanceSignColor = when {
            balance.toDouble() < 0.0 -> balanceColor
            else -> R.color.transparent
        }
        summaryBalanceSign.setTextColor(getColor(this, balanceSignColor))
    }

    private fun getCurrentMonthTransactionsSum(currentDate: LocalDate, predicate: DoublePredicate): BigDecimal {
        return transactions.stream()
            .filter { transaction ->
                transaction.date.monthValue == currentDate.monthValue && transaction.date.year == currentDate.year
            }
            .mapToDouble(Transaction::value)
            .filter(predicate)
            .sum()
            .toBigDecimal()
    }

    private fun handleAddTransactionResult(resultCode: Int, data: Intent) {
        if (resultCode != RESULT_OK) return
        val newTransaction = extractTransactionFromIntentData(data)
        transactions.add(newTransaction)
        transactionRecyclerView.adapter?.notifyDataSetChanged()
        updateMonthlySummary()
        thread {
            database.transactionDao().insert(newTransaction)
        }
    }

    private fun handleModifyTransactionResult(resultCode: Int, data: Intent) {
        if (resultCode != RESULT_OK) return
        val position = data.getIntExtra(INTENT_DATA_POSITION, -1)
        val id = transactions[position].id
        val updatedTransaction = extractTransactionFromIntentData(data, id)
        transactions[position] = updatedTransaction
        transactionRecyclerView.adapter?.notifyDataSetChanged()
        updateMonthlySummary()
        thread {
            database.transactionDao().update(updatedTransaction)
        }
    }

    fun handleRemoveTransaction(position: Int) {
        val transactionToRemove = transactions[position]
        transactions.remove(transactionToRemove)
        transactionRecyclerView.adapter?.notifyDataSetChanged()
        updateMonthlySummary()
        thread {
            database.transactionDao().delete(transactionToRemove)
        }
    }

    private fun extractTransactionFromIntentData(data: Intent, id: Int = 0): Transaction {
        val value = data.getDoubleExtra(INTENT_DATA_VALUE, 0.0)
        val date = data.getSerializableExtra(INTENT_DATA_DATE).toString().let { LocalDate.parse(it) }
        val category = data.getStringExtra(INTENT_DATA_CATEGORY).toString()
        val description = data.getStringExtra(INTENT_DESCRIPTION_DATA).toString()
        return Transaction(id, value, date, category, description)
    }

    private fun switchBottomView() {
        val fragmentToShow = if (showingTransactionList) allTransactionsFragment else monthBalanceFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainActivityBottomContainer, fragmentToShow, "MONTH_SUMMARY_GRAPH")
            .commit()
        val iconId = if (showingTransactionList) R.drawable.ic_balance_chart else R.drawable.ic_transaction_list
        fabMainActivityBottomPanelSwitcher?.setImageDrawable(getDrawable(applicationContext, iconId))
        showingTransactionList = !showingTransactionList
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
