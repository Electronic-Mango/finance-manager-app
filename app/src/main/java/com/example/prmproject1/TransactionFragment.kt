package com.example.prmproject1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime

/**
 * A [Fragment] representing a list of [Transaction].
 */
class TransactionFragment : Fragment() {

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
            adapter = TransactionRecyclerViewAdapter(getInitialTransactions(100))
        }
    }

}

fun getInitialTransactions(count: Int): List<Transaction> {
    val transactions: MutableList<Transaction> = ArrayList()
    for (transactionNumber: Int in 1 until count) {
        val transaction = Transaction(
            transactionNumber,
            transactionNumber,
            LocalDateTime.now(),
            "cat $transactionNumber",
            "desc $transactionNumber"
        )
        transactions.add(transaction)
    }
    return transactions
}
