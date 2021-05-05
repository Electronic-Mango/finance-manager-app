package com.prm.project1.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import com.prm.project1.R
import com.prm.project1.database.Transaction
import kotlinx.android.synthetic.main.fragment_month_balance_graph.*
import java.time.LocalDate
import java.time.Year

/**
 * [Fragment] displaying a graph all [Transaction] in a month.
 */
class MonthBalanceGraphFragment(private val transactions: List<Transaction>) : Fragment(), OnItemSelectedListener {
    private lateinit var possibleDates: List<String>
    private var pickedPosition = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_month_balance_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        graphDatePicker.onItemSelectedListener = this
        updateTransactions()
    }

    fun updateTransactions() {
        possibleDates = transactions.map { transactionToMonthAndYearString(it) }.distinct()
        ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, possibleDates).let {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            graphDatePicker.adapter = it
        }
        graphDatePicker.setSelection(pickedPosition)
    }

    private fun transactionToMonthAndYearString(transaction: Transaction): String {
        val year = transaction.date.year
        val month = String.format("%02d", transaction.date.month.value)
        return "$year-$month"
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val pickedDate = LocalDate.parse("${possibleDates[position]}-01")
        val pickedTransactions =
            transactions.filter { it.date.monthValue == pickedDate.monthValue && it.date.year == pickedDate.year }
        if (pickedTransactions.size < 2) {
            Snackbar.make(
                requireView(),
                "Wykres jest dostępny dla miesięcy z co najmniej dwoma transakcjami.",
                LENGTH_LONG
            ).setAction("Action", null).show()
            graphDatePicker.setSelection(pickedPosition)
        } else {
            val pickedMonthLength = pickedDate.month.length(Year.of(pickedDate.year).isLeap)
            transactionsGraphView.setTransactions(pickedTransactions, pickedMonthLength)
            pickedPosition = position
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit
}