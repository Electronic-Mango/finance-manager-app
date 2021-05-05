package com.prm.project1.addtransactionactivity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.prm.project1.Common.CATEGORIES
import com.prm.project1.Common.INTENT_DATA_CATEGORY
import com.prm.project1.Common.INTENT_DATA_DATE
import com.prm.project1.Common.INTENT_DATA_POSITION
import com.prm.project1.Common.INTENT_DATA_VALUE
import com.prm.project1.Common.INTENT_DESCRIPTION_DATA
import com.prm.project1.R
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.android.synthetic.main.content_add_transaction.*
import java.math.BigDecimal.ROUND_HALF_EVEN
import java.time.LocalDate

/**
 * [AppCompatActivity] responsible for creating new, or modifying [com.prm.project1.database.Transaction].
 */
class AddTransactionActivity : AppCompatActivity() {

    private var position: Int = 0
    private var value: Double = 0.0
    private var date: LocalDate = LocalDate.now()
    private lateinit var category: String
    private lateinit var description: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        setSupportActionBar(toolbarActivityAddTransaction)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        addTransactionFragmentDatePickerButton.text = LocalDate.now().toString()
        fabActivityAddTransaction.setOnClickListener(this::finishWithInputData)
        ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, CATEGORIES.keys.toList()).let {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            addTransactionFragmentCategory.adapter = it
        }
    }

    private fun finishWithInputData(view: View) {
        if (!isProvidedValueCorrect()) return
        val data = readAndParseValuesFromFieldsIntoIntent()
        setResult(RESULT_OK, data)
        finish()
    }

    override fun onStart() {
        super.onStart()
        position = intent.getIntExtra(INTENT_DATA_POSITION, -1).apply {
            if (this < 0) return
        }

        setTitle(R.string.title_activity_modify_transaction)
        val value = intent.getDoubleExtra(INTENT_DATA_VALUE, 0.0)
        chipGroup.check(if (value < 0) R.id.chipExpense else R.id.chipIncome)
        addTransactionFragmentValue.setText(value.toBigDecimal().abs().setScale(2).toPlainString())
        addTransactionFragmentDatePickerButton.text = intent.getStringExtra(INTENT_DATA_DATE).toString()
        addTransactionFragmentDescription.setText(intent.getStringExtra(INTENT_DESCRIPTION_DATA).toString())
        val category = intent.getStringExtra(INTENT_DATA_CATEGORY).toString()
        addTransactionFragmentCategory.setSelection(CATEGORIES.keys.toList().indexOf(category))

        readEditTextFieldsToValues()
    }

    private fun isProvidedValueCorrect(): Boolean {
        val valueText = addTransactionFragmentValue.text
        return if (valueText.isEmpty() || valueText.toString().toDouble() == 0.0) {
            addTransactionFragmentValue.requestFocus()
            addTransactionFragmentValue.error = "Podaj wartość transakcji!"
            false
        } else {
            true
        }
    }

    private fun readEditTextFieldsToValues() {
        var readValue = addTransactionFragmentValue.text.toString().toBigDecimal().setScale(2, ROUND_HALF_EVEN)
        if (chipExpense.isChecked) readValue = -readValue
        value = readValue.toDouble()
        date = addTransactionFragmentDatePickerButton.text.let { LocalDate.parse(it) }
        category = addTransactionFragmentCategory.selectedItem.toString()
        description = addTransactionFragmentDescription.text.toString()
    }

    private fun readAndParseValuesFromFieldsIntoIntent(): Intent {
        readEditTextFieldsToValues()
        return Intent().apply {
            putExtra(INTENT_DATA_POSITION, position)
            putExtra(INTENT_DATA_VALUE, value)
            putExtra(INTENT_DATA_DATE, date)
            putExtra(INTENT_DATA_CATEGORY, category)
            putExtra(INTENT_DESCRIPTION_DATA, description)
        }
    }

    fun pickTransactionDate(view: View) {
        val datePickerDialog = DatePickerDialog(view.context, { _, year, month, day ->
            LocalDate.of(year, month + 1, day).apply {
                addTransactionFragmentDatePickerButton.text = toString()
                date = this
            }
        }, date.year, date.monthValue - 1, date.dayOfMonth)
        datePickerDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_transaction, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share -> {
                shareTransaction()
                true
            }
            android.R.id.home -> {
                setResult(RESULT_CANCELED)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareTransaction() {
        if (!isProvidedValueCorrect()) return
        readEditTextFieldsToValues()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Transakcja: $value, $date, $category, $description")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Udostępnij transakcję")
        startActivity(shareIntent)
    }
}
