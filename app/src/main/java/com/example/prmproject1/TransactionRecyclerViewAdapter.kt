package com.example.prmproject1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_transaction.view.*

/**
 * [RecyclerView.Adapter] that can display a [Transaction].
 */
class TransactionRecyclerViewAdapter(
    private val transactions: MutableList<Transaction>
) : RecyclerView.Adapter<TransactionRecyclerViewAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[transactions.size - position - 1]
        holder.valueView.text = transaction.value.toString()
        holder.dateView.text = transaction.date.toString()
        holder.categoryView.text = transaction.category
        holder.descriptionView.text = transaction.description
    }

    override fun getItemCount(): Int = transactions.size

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val valueView: TextView = view.transactionFragmentValue
        val dateView: TextView = view.transactionFragmentDate
        val categoryView: TextView = view.transactionFragmentCategory
        val descriptionView: TextView = view.transactionFragmentDescription
    }

}
