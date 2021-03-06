package com.lazureleming.financemanager.mainactivity

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.lazureleming.financemanager.R
import com.lazureleming.financemanager.database.Transaction
import kotlin.reflect.KClass

/**
 * A [FragmentPagerAdapter] handling main tabs of the application - [TransactionsListFragment] and [MonthBalanceGraphFragment].
 */
class SectionsPagerAdapter(
    private val context: Context,
    transactions: List<Transaction>,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val transactionsListFragment by lazy { TransactionsListFragment(transactions) }
    private val monthBalanceGraph by lazy { MonthBalanceGraphFragment(transactions) }
    private val pages by lazy {
        arrayOf(
            Pair(R.string.all_transaction_list_tab, transactionsListFragment),
            Pair(R.string.month_transaction_graph_tab, monthBalanceGraph)
        )
    }

    override fun getItem(position: Int): Fragment = pages[position].second

    override fun getPageTitle(position: Int): String = context.getString(pages[position].first)

    override fun getCount(): Int = pages.size

    fun updateTransactions() {
        transactionsListFragment.updateTransactions()
        monthBalanceGraph.updateTransactions()
    }

    fun fragmentPosition(kClass: KClass<out Fragment>): Int {
        pages.forEachIndexed { index, page -> if (kClass.isInstance(page.second)) return index }
        throw IllegalArgumentException("kClass does not represent stored fragment! Provided: $kClass, stored: $pages.")
    }
}