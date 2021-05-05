package com.example.prmproject1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prmproject1.database.Transaction

/**
 * A [Fragment] representing a list of [Transaction].
 */
class TransactionListFragment(private val transactions: MutableList<Transaction>) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return (inflater.inflate(R.layout.fragment_transaction_list, container, false) as RecyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TransactionRecyclerViewAdapter(transactions)
        }
    }
}
