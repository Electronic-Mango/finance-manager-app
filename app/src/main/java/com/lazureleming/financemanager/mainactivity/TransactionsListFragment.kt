package com.lazureleming.financemanager.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazureleming.financemanager.R
import com.lazureleming.financemanager.database.Transaction
import kotlinx.android.synthetic.main.fragment_transactions_list.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import java.util.function.DoublePredicate

/**
 * Fragment representing main screen of the application - [TransactionRecyclerViewAdapter] of [Transaction]
 * and summary of current month.
 */
class TransactionsListFragment(private val transactions: List<Transaction>) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transactions_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = TransactionRecyclerViewAdapter(transactions)
        }
        updateMonthlySummary()
    }

    fun updateTransactions() {
        transactionRecyclerView?.adapter?.notifyDataSetChanged()
        updateMonthlySummary()
    }

    private fun updateMonthlySummary() {
        val currentDate = LocalDate.now()
        val currentMonth = currentDate.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("pl"))
        summaryCurrentMonth?.text = getString(R.string.summary_current_month_text, currentMonth, currentDate.year)
        updateMonthlySummaryValues(currentDate)
    }

    private fun updateMonthlySummaryValues(currentDate: LocalDate) {
        val income = getCurrentMonthTransactionsSum(currentDate) { value -> value > 0.0 }
        val expenses = getCurrentMonthTransactionsSum(currentDate) { value -> value < 0.0 }
        val balance = income + expenses

        summaryCurrentIncome?.text = income.toPlainString()
        summaryCurrentExpenses?.text = expenses.abs().toPlainString()
        summaryCurrentBalance?.text = balance.abs().toPlainString()
        val balanceColor = when {
            balance.toDouble() >= 0.0 -> R.color.balance_positive
            else -> R.color.balance_negative
        }
        summaryCurrentBalance?.setTextColor(ContextCompat.getColor(requireContext(), balanceColor))
        val balanceSignColor = when {
            balance.toDouble() < 0.0 -> balanceColor
            else -> R.color.transparent
        }
        summaryBalanceSign?.setTextColor(ContextCompat.getColor(requireContext(), balanceSignColor))

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
            .setScale(2)
    }
}