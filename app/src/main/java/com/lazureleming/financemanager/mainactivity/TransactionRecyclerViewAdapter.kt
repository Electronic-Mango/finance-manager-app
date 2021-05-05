package com.lazureleming.financemanager.mainactivity

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
import com.lazureleming.financemanager.Common.CATEGORIES
import com.lazureleming.financemanager.Common.INTENT_DATA_CATEGORY
import com.lazureleming.financemanager.Common.INTENT_DATA_DATE
import com.lazureleming.financemanager.Common.INTENT_DATA_POSITION
import com.lazureleming.financemanager.Common.INTENT_DATA_VALUE
import com.lazureleming.financemanager.Common.INTENT_PLACE_DATA
import com.lazureleming.financemanager.R
import com.lazureleming.financemanager.addtransactionactivity.AddTransactionActivity
import com.lazureleming.financemanager.database.Transaction
import com.lazureleming.financemanager.mainactivity.TransactionRecyclerViewAdapter.TransactionViewHolder
import kotlinx.android.synthetic.main.fragment_transaction.view.*

/**
 * [RecyclerView.Adapter] that can display list of [Transaction].
 */
class TransactionRecyclerViewAdapter(private val transactions: List<Transaction>) : Adapter<TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position], position)
    }

    override fun getItemCount(): Int = transactions.size

    inner class TransactionViewHolder(view: View) : ViewHolder(view), OnClickListener, OnLongClickListener {
        private var transactionPosition = 0
        private val valueView = view.transactionFragmentValue
        private val dateView = view.transactionFragmentDate
        private val categoryView = view.transactionFragmentCategory
        private val placeView = view.transactionFragmentPlace

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        fun bind(transaction: Transaction, position: Int) {
            transactionPosition = position
            valueView.text = transaction.value.toBigDecimal().setScale(2).toPlainString()
            setValueViewColor(transaction.value)
            dateView.text = transaction.date.toString()
            categoryView.text = transaction.category
            placeView.text = transaction.place
            CATEGORIES[transaction.category]?.let {
                itemView.categoryIndicator.background.setTint(getColor(itemView.context, it))
            }
        }

        private fun setValueViewColor(value: Double) {
            val colorId = if (value > 0.0) R.color.balance_positive else R.color.balance_negative
            val color = getColor(valueView.context, colorId)
            valueView.setTextColor(color)
        }

        override fun onClick(v: View) {
            val intent = Intent(itemView.context, AddTransactionActivity::class.java).apply {
                putExtra(INTENT_DATA_POSITION, transactionPosition)
                putExtra(INTENT_DATA_VALUE, valueView.text.toString().toDouble())
                putExtra(INTENT_DATA_DATE, dateView.text.toString())
                putExtra(INTENT_DATA_CATEGORY, categoryView.text.toString())
                putExtra(INTENT_PLACE_DATA, placeView.text.toString())
            }
            (itemView.context as MainActivity).modifyTransaction(intent)
        }

        override fun onLongClick(v: View): Boolean {
            AlertDialog.Builder(itemView.context)
                .setMessage("Na pewno chcesz usunąć wpis?")
                .setCancelable(false)
                .setPositiveButton("Tak") { _, _ ->
                    (itemView.context as MainActivity).handleRemoveTransaction(transactionPosition)
                }
                .setNegativeButton("Nie") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
            return true
        }
    }
}
