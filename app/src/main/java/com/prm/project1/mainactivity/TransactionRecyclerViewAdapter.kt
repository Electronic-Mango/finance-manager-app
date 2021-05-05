package com.prm.project1.mainactivity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.prm.project1.Common.CATEGORIES
import com.prm.project1.Common.INTENT_DATA_CATEGORY
import com.prm.project1.Common.INTENT_DATA_DATE
import com.prm.project1.Common.INTENT_DATA_POSITION
import com.prm.project1.Common.INTENT_DATA_VALUE
import com.prm.project1.Common.INTENT_DESCRIPTION_DATA
import com.prm.project1.Common.MODIFY_TRANSACTION_REQUEST_CODE
import com.prm.project1.R
import com.prm.project1.addtransactionactivity.AddTransactionActivity
import com.prm.project1.database.Transaction
import kotlinx.android.synthetic.main.fragment_transaction.view.*

/**
 * [RecyclerView.Adapter] that can display a [Transaction].
 */
class TransactionRecyclerViewAdapter(private val transactions: MutableList<Transaction>) :
    Adapter<TransactionRecyclerViewAdapter.TransactionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[transactions.size - position - 1]
        holder.bind(transaction, transactions.indexOf(transaction))
    }

    override fun getItemCount(): Int = transactions.size

    inner class TransactionViewHolder(view: View) : ViewHolder(view), OnClickListener, OnLongClickListener {
        private var pos = 0
        private val valueView = view.transactionFragmentValue
        private val dateView = view.transactionFragmentDate
        private val categoryView = view.transactionFragmentCategory
        private val descriptionView = view.transactionFragmentDescription

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        fun bind(transaction: Transaction, position: Int) {
            pos = position
            valueView.text = transaction.value.toBigDecimal().toPlainString()
            valueView.setTextColor(
                getColor(
                    valueView.context,
                    if (transaction.value > 0) R.color.balance_positive else R.color.balance_negative
                )
            )
            dateView.text = transaction.date.toString()
            categoryView.text = transaction.category
            descriptionView.text = transaction.description
            CATEGORIES[transaction.category]?.let {
                itemView.categoryIndicator.background.setTint(getColor(itemView.context, it))
            }
        }

        override fun onClick(v: View) {
            val intent = Intent(itemView.context, AddTransactionActivity::class.java).apply {
                putExtra(INTENT_DATA_POSITION, pos)
                putExtra(INTENT_DATA_VALUE, valueView.text.toString().toDouble())
                putExtra(INTENT_DATA_DATE, dateView.text.toString())
                putExtra(INTENT_DATA_CATEGORY, categoryView.text.toString())
                putExtra(INTENT_DESCRIPTION_DATA, descriptionView.text.toString())
            }
            (itemView.context as Activity).startActivityForResult(intent, MODIFY_TRANSACTION_REQUEST_CODE)
        }

        override fun onLongClick(v: View): Boolean {
            AlertDialog.Builder(itemView.context)
                .setMessage("Na pewno chcesz usunąć wpis?")
                .setCancelable(false)
                .setPositiveButton("Tak") { _, _ -> (itemView.context as MainActivity).handleRemoveTransaction(pos) }
                .setNegativeButton("Nie") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
            return true
        }
    }
}
