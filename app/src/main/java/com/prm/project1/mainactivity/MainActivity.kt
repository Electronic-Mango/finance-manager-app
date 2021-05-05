package com.prm.project1.mainactivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.prm.project1.Common.ADD_TRANSACTION_REQUEST_CODE
import com.prm.project1.Common.DB_NAME
import com.prm.project1.Common.INTENT_DATA_CATEGORY
import com.prm.project1.Common.INTENT_DATA_DATE
import com.prm.project1.Common.INTENT_DATA_POSITION
import com.prm.project1.Common.INTENT_DATA_VALUE
import com.prm.project1.Common.INTENT_PLACE_DATA
import com.prm.project1.Common.MODIFY_TRANSACTION_REQUEST_CODE
import com.prm.project1.R
import com.prm.project1.addtransactionactivity.AddTransactionActivity
import com.prm.project1.database.Transaction
import com.prm.project1.database.TransactionDatabase
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import kotlin.concurrent.thread

/**
 * Main [AppCompatActivity] of the app, displays all recorded transactions.
 */
class MainActivity : AppCompatActivity() {

    private val transactions = mutableListOf<Transaction>()
    private lateinit var database: TransactionDatabase
    private val sectionsPagerAdapter by lazy { SectionsPagerAdapter(this, transactions, supportFragmentManager) }
    private var newActivityLaunched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Room.databaseBuilder(this, TransactionDatabase::class.java, DB_NAME).build()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbarActivityMain)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        thread {
            val initialTransactions = database.transactionDao().getAll()
            runOnUiThread {
                transactions.addAll(initialTransactions)
                transactions.sortByDescending { it.date }
                configureTabsAndViewPager()
                fabActivityMain.setOnClickListener(this::addNewTransaction)
            }
        }
    }

    private fun configureTabsAndViewPager() {
        viewPager.adapter = sectionsPagerAdapter
        mainActivityTabs.setupWithViewPager(viewPager)
        for (tabIndex in 0 until mainActivityTabs.tabCount) {
            mainActivityTabs.getTabAt(tabIndex)?.setIcon(getCorrectTabIcon(tabIndex))
        }
        viewPager.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    sectionsPagerAdapter.fragmentPosition(TransactionsListFragment::class) -> fabActivityMain.show()
                    sectionsPagerAdapter.fragmentPosition(MonthBalanceGraphFragment::class) -> fabActivityMain.hide()
                }
            }
        })
    }

    private fun getCorrectTabIcon(tabIndex: Int) = when (tabIndex) {
        sectionsPagerAdapter.fragmentPosition(TransactionsListFragment::class) -> R.drawable.ic_transaction_list
        sectionsPagerAdapter.fragmentPosition(MonthBalanceGraphFragment::class) -> R.drawable.ic_balance_chart
        else -> throw IllegalArgumentException("Unexpected tab!")
    }

    private fun addNewTransaction(view: View) {
        if (newActivityLaunched) return
        newActivityLaunched = true
        val intent = Intent(this, AddTransactionActivity::class.java)
        startActivityForResult(intent, ADD_TRANSACTION_REQUEST_CODE)
    }

    fun modifyTransaction(intent: Intent) {
        if (newActivityLaunched) return
        newActivityLaunched = true
        startActivityForResult(intent, MODIFY_TRANSACTION_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ADD_TRANSACTION_REQUEST_CODE -> data?.let { handleAddTransactionResult(resultCode, it) }
            MODIFY_TRANSACTION_REQUEST_CODE -> data?.let { handleModifyTransactionResult(resultCode, it) }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
        newActivityLaunched = false
    }

    private fun handleAddTransactionResult(resultCode: Int, data: Intent) {
        if (resultCode != RESULT_OK) return
        val newTransaction = extractTransactionFromIntentData(data)
        thread {
            val newId = database.transactionDao().insert(newTransaction)
            val transactionWithId = Transaction(
                newId, newTransaction.value, newTransaction.date,
                newTransaction.category, newTransaction.place
            )
            runOnUiThread {
                transactions.add(transactionWithId)
                transactions.sortByDescending { it.date }
                sectionsPagerAdapter.updateTransactions()
            }
        }
    }

    private fun handleModifyTransactionResult(resultCode: Int, data: Intent) {
        if (resultCode != RESULT_OK) return
        val position = data.getIntExtra(INTENT_DATA_POSITION, -1)
        val id = transactions[position].id
        val updatedTransaction = extractTransactionFromIntentData(data, id)
        thread {
            database.transactionDao().update(updatedTransaction)
            runOnUiThread {
                transactions[position] = updatedTransaction
                transactions.sortByDescending { it.date }
                sectionsPagerAdapter.updateTransactions()
            }
        }
    }

    fun handleRemoveTransaction(position: Int) {
        val transactionToRemove = transactions[position]
        thread {
            database.transactionDao().delete(transactionToRemove)
            runOnUiThread {
                transactions.remove(transactionToRemove)
                sectionsPagerAdapter.updateTransactions()
            }
        }
    }

    private fun extractTransactionFromIntentData(data: Intent, id: Long = 0): Transaction {
        val value = data.getDoubleExtra(INTENT_DATA_VALUE, 0.0)
        val date = data.getSerializableExtra(INTENT_DATA_DATE).toString().let { LocalDate.parse(it) }
        val category = data.getStringExtra(INTENT_DATA_CATEGORY).toString()
        val place = data.getStringExtra(INTENT_PLACE_DATA).toString()
        return Transaction(id, value, date, category, place)
    }
}
