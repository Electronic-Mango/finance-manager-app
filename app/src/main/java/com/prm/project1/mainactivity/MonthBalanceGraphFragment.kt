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
import java.time.format.TextStyle.FULL_STANDALONE
import java.util.*

/**
 * [Fragment] displaying a graph all [Transaction] in a month.
 */
class MonthBalanceGraphFragment(private val transactions: List<Transaction>) : Fragment(), OnItemSelectedListener {

    private lateinit var possibleDates: List<SpinnerData>
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
        possibleDates = transactions.map { SpinnerData(it.date) }.distinct()
        context?.let { context ->
            ArrayAdapter(context, android.R.layout.simple_spinner_item, possibleDates).let {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                graphDatePicker.adapter = it
            }
            graphDatePicker.setSelection(pickedPosition)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val pickedDate = possibleDates[position].toLocalDate()
        val pickedTransactions =
            transactions.filter { it.date.monthValue == pickedDate.monthValue && it.date.year == pickedDate.year }
        if (pickedTransactions.groupBy { it.date }.size < 2) {
            balanceGraphUnavailableMessage.visibility = View.VISIBLE
            transactionsGraphView.visibility = View.INVISIBLE
        } else {
            balanceGraphUnavailableMessage.visibility = View.INVISIBLE
            transactionsGraphView.visibility = View.VISIBLE
            val pickedMonthLength = pickedDate.month.length(Year.of(pickedDate.year).isLeap)
            transactionsGraphView.setTransactions(pickedTransactions, pickedMonthLength)
            pickedPosition = position
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit
}

private class SpinnerData(date: LocalDate) {
    private val month = date.month
    private val year = date.year

    fun toLocalDate(): LocalDate = LocalDate.of(year, month, 1)

    override fun toString(): String {
        val monthString = month.getDisplayName(FULL_STANDALONE, Locale("pl")).toUpperCase(Locale.ROOT)
        return "$monthString $year"
    }

    override fun hashCode(): Int = Objects.hash(month, year)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is SpinnerData) return false
        return month == other.month && year == other.year
    }
}