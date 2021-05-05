package com.example.prmproject1

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.prmproject1.Common.CATEGORY_DATA_RESULT
import com.example.prmproject1.Common.DATE_DATA_RESULT
import com.example.prmproject1.Common.DESCRIPTION_DATA_RESULT
import com.example.prmproject1.Common.VALUE_DATA_RESULT
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.android.synthetic.main.content_add_transaction.*
import java.time.LocalDate

/**
 * [AppCompatActivity] responsible for creating new [Transaction].
 */
class AddTransactionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        setSupportActionBar(toolbarActivityAddTransaction)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        addTransactionFragmentDatePickerButton.text = LocalDate.now().toString()

        fabActivityAddTransaction.setOnClickListener {
            val data = readAndParseValuesFromFields()
            setResult(RESULT_OK, data)
            finish()
        }
    }

    private fun readAndParseValuesFromFields(): Intent {
        val value = addTransactionFragmentValue.text.toString().toFloat()
        val date = addTransactionFragmentDatePickerButton.text.let { LocalDate.parse(it) }
        val category = addTransactionFragmentCategory.text.toString()
        val description = addTransactionFragmentDescription.text.toString()
        return Intent().apply {
            putExtra(VALUE_DATA_RESULT, value)
            putExtra(DATE_DATA_RESULT, date)
            putExtra(CATEGORY_DATA_RESULT, category)
            putExtra(DESCRIPTION_DATA_RESULT, description)
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
