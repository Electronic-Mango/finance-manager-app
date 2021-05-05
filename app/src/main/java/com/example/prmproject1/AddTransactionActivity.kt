package com.example.prmproject1

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.prmproject1.Common.CATEGORIES
import com.example.prmproject1.Common.INTENT_DATA_CATEGORY
import com.example.prmproject1.Common.INTENT_DATA_DATE
import com.example.prmproject1.Common.INTENT_DESCRIPTION_DATA
import com.example.prmproject1.Common.INTENT_DATA_POSITION
import com.example.prmproject1.Common.INTENT_DATA_VALUE
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.android.synthetic.main.content_add_transaction.*
import java.time.LocalDate
import kotlin.math.abs

/**
 * [AppCompatActivity] responsible for creating new [Transaction].
 */
class AddTransactionActivity : AppCompatActivity() {

    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        setSupportActionBar(toolbarActivityAddTransaction)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        addTransactionFragmentDatePickerButton.text = LocalDate.now().toString()

        ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            CATEGORIES.keys.toList()
        ).let {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            addTransactionFragmentCategory.adapter = it
        }

        fabActivityAddTransaction.setOnClickListener {
            if (addTransactionFragmentValue.text.isEmpty() || addTransactionFragmentValue.text.toString().toDouble() == 0.0) {
                addTransactionFragmentValue.requestFocus()
                addTransactionFragmentValue.error = "Podaj wartość transakcji!"
                return@setOnClickListener
            }
            val data = readAndParseValuesFromFields()
            setResult(RESULT_OK, data)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        position = intent.getIntExtra(INTENT_DATA_POSITION, -1).apply {
            if (this < 0) {
                return
            }
        }
        setTitle(R.string.title_activity_modify_transaction)
        val value = intent.getDoubleExtra(INTENT_DATA_VALUE, 0.0)
        radioGroup.check(if (value < 0) R.id.radioButtonExpense else R.id.radioButtonIncome)
        addTransactionFragmentValue.setText(abs(value).toString())
        addTransactionFragmentDatePickerButton.text = intent.getStringExtra(INTENT_DATA_DATE).toString()
        addTransactionFragmentDescription.setText(intent.getStringExtra(INTENT_DESCRIPTION_DATA).toString())
        val category = intent.getStringExtra(INTENT_DATA_CATEGORY).toString()
        addTransactionFragmentCategory.setSelection(CATEGORIES.keys.toList().indexOf(category))
    }

    private fun readAndParseValuesFromFields(): Intent {
        val value = addTransactionFragmentValue.text.toString().toDouble()
        val date = addTransactionFragmentDatePickerButton.text.let { LocalDate.parse(it) }
        val category = addTransactionFragmentCategory.selectedItem.toString()
        val description = addTransactionFragmentDescription.text.toString()
        return Intent().apply {
            putExtra(INTENT_DATA_POSITION, position)
            putExtra(INTENT_DATA_VALUE, if (radioButtonIncome.isChecked) value else -value)
            putExtra(INTENT_DATA_DATE, date)
            putExtra(INTENT_DATA_CATEGORY, category)
            putExtra(INTENT_DESCRIPTION_DATA, description)
        }
    }

    fun pickTransactionDate(view: View) {
        val currentDate = LocalDate.now()
        val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
            LocalDate.of(year, month + 1, day).apply {
                addTransactionFragmentDatePickerButton.text = toString()
            }
        }, currentDate.year, currentDate.monthValue, currentDate.dayOfMonth)
        datePickerDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_CANCELED)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
