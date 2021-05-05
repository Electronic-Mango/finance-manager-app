package com.example.prmproject1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * A [Fragment] representing a list of [Transaction].
 */
class TransactionListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return (inflater.inflate(
            R.layout.fragment_transaction_list,
            container,
            false
        ) as RecyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TransactionRecyclerViewAdapter(getInitialTransactions(10))
        }
    }

    fun addTransaction(transaction: Transaction) {
        Log.d("TAG", "MSG")
    }

}

fun getInitialTransactions(count: Int): MutableList<Transaction> {
    val transactions: MutableList<Transaction> = ArrayList()
    for (transactionNumber: Int in 1 until count) {
        val transaction = Transaction(
            transactionNumber,
            transactionNumber.toFloat(),
            LocalDate.now(),
            "cat $transactionNumber",
            "desc $transactionNumber"
        )
        transactions.add(transaction)
    }
    return transactions
}
